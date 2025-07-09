import React, {useState} from 'react';
import {useAuth} from './contexts/AuthContext';
import {Navigate} from 'react-router-dom';

interface ProtectedRouteProps {
    anyAuthority?: string[];
    allAuthorities?: string[];
    noneAuthorities?: string[];
}

export const ProtectedRoute: React.FC<{ children: React.ReactNode, props?: ProtectedRouteProps }> = ({ children, props }) => {
    const {user, loading} = useAuth();
    const [allowed, setAllowed] = useState(true);

    if (user) {
        if (props?.anyAuthority && user.authorities.filter(authority => props.anyAuthority!.includes(authority)).length < 1) {
            setAllowed(false);
        }
        if (props?.allAuthorities && user.authorities.filter(authority => props.allAuthorities!.includes(authority)).length != props.allAuthorities!.length) {
            setAllowed(false);
        }
        if (props?.noneAuthorities && user.authorities.filter(authority => props.noneAuthorities!.includes(authority)).length >= 1) {
            setAllowed(false);
        }
    }

    if (loading) return <div>Loading...</div>;
    if (!user) return <Navigate to="/login" replace/>;
    if (!allowed) return <div>User does not have enough access</div>

    return <>{children}</>;
}; 