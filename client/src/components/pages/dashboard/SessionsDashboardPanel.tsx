import React, {useCallback, useEffect, useState} from 'react';
import {SessionResponse} from "../../../types/dashboardTypes";
import {useApi} from "../../contexts/ApiContext";
import styles from '../../../styles/Dashboard.module.css';
import {useAuth} from "../../contexts/AuthContext";

interface SessionsDashboardPanelProps {

}

const SessionsDashboardPanel: React.FC<SessionsDashboardPanelProps> = (props) => {
    const [sessions, setSessions] = useState<Array<SessionResponse>>([]);
    const {api} = useApi();
    const {user} = useAuth();

    const sortSessionsFunction = useCallback((a: SessionResponse, b: SessionResponse) => {
        // Current session first
        if (a.id === user?.sessionId) return -1;
        if (b.id === user?.sessionId) return 1;

        // Non-revoked before revoked
        if (!a.revoked && b.revoked) return -1;
        if (a.revoked && !b.revoked) return 1;

        // Optional: fallback to createdAt or id for consistent sort
        return a.createdAt.getTime() - b.createdAt.getTime();
    }, [user]);


    useEffect(() => {
        api.get('/public/auth/sessions/active')
            .then(response => {
                const sessionsResponse = response.data as Array<SessionResponse>;
                setSessions(sessionsResponse.map((s => ({
                    ...s,
                    createdAt: new Date(s.createdAt),
                    expiresAt: new Date(s.expiresAt)
                }))).sort(sortSessionsFunction));
            });
    }, [user]);

    const revokeCallback = useCallback(async (sessionId: string) => {
        return api.delete(`/public/auth/sessions/revoke/${sessionId}`)
            .then(() => {
                const updatedSessions = sessions.map(session => session.id != sessionId ? session : {
                    ...session,
                    revoked: true
                }).sort(sortSessionsFunction);
                setSessions(updatedSessions);
            })
            .catch(() => {
            });
    }, [sessions]);

    return (
        <div className={styles.sessionPanel}>
            {sessions.map(session => {
                return (
                    <div className={`${styles.sessionContainer} ${session.revoked ? styles.revokedSessionContainer : ''}`} key={session.id}>
                        <div className={styles.sessionInfo}>
                            <h3>Session identifier: {session.id} {session.id === user?.sessionId && '(current)'}</h3>
                            <div><strong>IP:</strong> {session.ipAddress}</div>
                            <div><strong>OS:</strong> {session.osSystem}</div>
                            <div><strong>Browser:</strong> {session.browser}</div>
                            <div><strong>Created at:</strong> {session.createdAt.toLocaleDateString()}</div>
                            <div><strong>Expires at:</strong> {session.expiresAt.toLocaleDateString()}</div>
                            <div><strong>Revoked:</strong> {JSON.stringify(session.revoked)}</div>
                        </div>

                        <button className={styles.revokeButton}
                                disabled={session.revoked || user?.sessionId === session.id}
                                onClick={() => revokeCallback(session.id)}>

                            Revoke
                        </button>
                    </div>
                )
            })}
        </div>
    );
};

export default SessionsDashboardPanel;