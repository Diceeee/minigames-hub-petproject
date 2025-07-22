import React, {useState} from 'react';
import {useAuth} from '../../contexts/AuthContext';
import styles from '../../../styles/Dashboard.module.css';
import {DashboardPanelTypes} from "../../../types/dashboardTypes";
import Sessions from "./SessionsDashboardPanel";
import ProfileDashboardPanel from "./ProfileDashboardPanel";

const Dashboard: React.FC = () => {
    const {user} = useAuth();
    const [activePanel, setActivePanel] = useState<DashboardPanelTypes>(DashboardPanelTypes.PROFILE);

    return (
        <div className={styles.dashboardContainer}>
            <div className={styles.dashboardHeaderContainer}>
                <h2>Welcome to your Dashboard{user ? `, ${user.username}` : ''}!</h2>
                <p>Here you can control your active sessions and revoke them</p>
            </div>
            <div className={styles.dashboardContentContainer}>
                <div className={styles.dashboardContentPanelList}>
                    <div
                        className={`${styles.dashboardContentPanelListItem} ${activePanel === DashboardPanelTypes.PROFILE ? styles.activePanelListItem : ''}`}
                        onClick={() => setActivePanel(DashboardPanelTypes.PROFILE)}>

                        Profile
                    </div>
                    <div
                        className={`${styles.dashboardContentPanelListItem} ${activePanel === DashboardPanelTypes.SESSIONS ? styles.activePanelListItem : ''}`}
                        onClick={() => setActivePanel(DashboardPanelTypes.SESSIONS)}>

                        Sessions
                    </div>
                </div>
                <div className={styles.dashboardPanelContent}>
                    {activePanel === DashboardPanelTypes.PROFILE
                        && <ProfileDashboardPanel/>}
                    {activePanel === DashboardPanelTypes.SESSIONS
                        && <Sessions/>}
                </div>
            </div>
        </div>
    )
};

export default Dashboard;