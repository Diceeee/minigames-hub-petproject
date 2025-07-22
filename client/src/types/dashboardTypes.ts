export interface SessionResponse {
    id: string,
    osSystem: string,
    browser: string,
    ipAddress: string,
    createdAt: Date,
    expiresAt: Date,
    revoked: boolean,
}

export enum DashboardPanelTypes {
    PROFILE,
    SESSIONS
}