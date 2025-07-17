import React, {createContext, RefObject, useCallback, useContext, useEffect, useMemo, useRef} from 'react';
import axios, {AxiosInstance, InternalAxiosRequestConfig} from "axios";
import {API_BASE_URL} from "../../api/urls";
import Cookies from "js-cookie";

type ApiContextType = {
    api: AxiosInstance,
    tryRefreshOnAccessDenied: RefObject<boolean>,
}

const ApiContext = createContext<ApiContextType | undefined>(undefined);

export const ApiProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const csrfToken = Cookies.get('XSRF-TOKEN');
    const tryRefreshOnAccessDenied = useRef(true);

    // Use refs to always have the latest callback logic
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
    const refreshInterceptor = useRef<null | ((error: any) => Promise<any>)>(null);
    refreshInterceptor.current = async (error: any) => {
        if (error.response &&
            (error.response.status === 401 || error.response.status === 403) &&
            tryRefreshOnAccessDenied.current
        ) {
            try {
                tryRefreshOnAccessDenied.current = false;
                await api.post('/public/auth/refresh', null);
                tryRefreshOnAccessDenied.current = true;
                return api(error.config);
            } catch (e) {
                // Optionally handle refresh failure
            }
        }
        return Promise.reject(error);
    };

    // Create Axios instance and add interceptors only once
    const api = useMemo(() => {
        const instance = axios.create({
            baseURL: API_BASE_URL,
            withCredentials: true,
        });
        // Request interceptor uses xsrfRef
        instance.interceptors.request.use(
            (config) => xsrfRef.current(config),
            (error) => Promise.reject(error)
        );
        // Response interceptor uses refreshInterceptor
        instance.interceptors.response.use(
            response => response,
            (error) => refreshInterceptor.current ? refreshInterceptor.current(error) : Promise.reject(error)
        );
        return instance;
    }, []);

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