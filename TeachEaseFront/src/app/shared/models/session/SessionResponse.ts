export interface SessionResponse {
    id?: string;
    title?: string;
    description?: string;
    scheduledTime?: Date;
    url?: string;
    location?: string;
    latitude?: number;
    longitude?: number;
    group?: string;
}