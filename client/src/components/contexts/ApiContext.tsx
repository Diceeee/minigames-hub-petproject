import React, {createContext, RefObject, useCallback, useContext, useEffect, useMemo, useRef} from 'react';
import axios, {AxiosInstance, InternalAxiosRequestConfig} from "axios";
import {API_BASE_URL} from "../../api/urls";
import Cookies from "js-cookie";
import {useNavigate} from "react-router-dom";

type ApiContextType = {
    api: AxiosInstance,
    tryRefreshOnAccessDenied: RefObject<boolean>,
}

const ApiContext = createContext<ApiContextType | undefined>(undefined);

export const ApiProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const csrfToken = Cookies.get('XSRF-TOKEN');
    const tryRefreshOnAccessDenied = useRef(true);
    const refreshPromiseRef = useRef<Promise<any> | null>(null);
    const navigate = useNavigate();

    const xsrfCallback = useCallback((config: InternalAxiosRequestConfig) => {
        if (csrfToken) {
            config.headers['X-XSRF-TOKEN'] = csrfToken;
        }
        return config;
    }, [csrfToken]);
    const xsrfRef = useRef(xsrfCallback);
    useEffect(() => {
        xsrfRef.current = xsrfCallback;
    }, [xsrfCallback]);

    // Response interceptor refresh logic as ref
    const refInterceptor = useCallback(async (err: any) => {
        if (err.response && (err.response.status === 401 || err.response.status === 403 && tryRefreshOnAccessDenied.current)) {
            if (!refreshPromiseRef.current) {
                refreshPromiseRef.current = api.post('/public/auth/refresh');
            }

            try {
                await refreshPromiseRef.current;
                return api(err.config);
            } catch (e: any) {
                navigate('/login', {state: {errorMessage: 'You are logged out. Please, log in!'}});
            } finally {
                refreshPromiseRef.current = null;
            }
        }

        return Promise.reject(err);
    }, []);

    // Create Axios instance and add interceptors only once
    const api = useMemo(() => {
        const instance = axios.create({
            baseURL: API_BASE_URL,
            withCredentials: true,
        });
        instance.interceptors.request.use(
            (config) => xsrfRef.current(config),
            (error) => Promise.reject(error)
        );
        instance.interceptors.response.use(
            response => response,
            (error) => refInterceptor ? refInterceptor(error) : Promise.reject(error)
        );
        return instance;
    }, [refInterceptor]);

    return (
        <ApiContext.Provider value={{api, tryRefreshOnAccessDenied}}>
            {children}
        </ApiContext.Provider>
    );
};

export const useApi = () => {
    const ctx = useContext(ApiContext);
    if (!ctx) throw new Error('useApi must be used within ApiProvider');
    return ctx;
};