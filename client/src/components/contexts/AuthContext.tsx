import React, {createContext, useContext, useEffect, useState} from 'react';
import axios from 'axios';
import {User} from "api/types";
import {useLocation, useNavigate} from "react-router-dom";
import {useApi} from "./ApiContext";

type AuthContextType = {
    user: User | null;
    loading: boolean;
    error: string | null;
    refresh: () => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const {api, tryRefreshOnAccessDenied} = useApi();
    const location = useLocation();
    const navigate = useNavigate();
    const userIsInRegistrationProgress = user?.authorities.length == 0;

    useEffect(() => {
        if (userIsInRegistrationProgress && location.pathname !== "/register") {
            navigate("/register");
        }
    }, [userIsInRegistrationProgress, location.pathname, navigate]);

    const fetchProfile = async (): Promise<void> => {
        setLoading(true);
        setError(null);

        try {
            console.log("User me called");
            const res = await api.get<User>('/auth/user/me', {withCredentials: true});
            tryRefreshOnAccessDenied.current = true;
            setUser(res.data);
        } catch (e: unknown) {
            if (axios.isAxiosError(e) && e.response) {
                setUser(null);
            } else {
                setError('Network error');
                setUser(null);
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProfile();
    }, []);

    useEffect(() => {
        api.get("/csrf").catch(error => console.log(error));
    }, []);

    const refresh = fetchProfile;
    const logout = async () => {
        await api.post('auth/logout', null, {withCredentials: true});
        tryRefreshOnAccessDenied.current = false;
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{user, loading, error, refresh, logout}}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}; 