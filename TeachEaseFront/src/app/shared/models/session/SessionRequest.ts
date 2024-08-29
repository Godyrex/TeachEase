export interface SessionRequest {
    title?: string;
    description?: string;
    scheduledTime?: Date;
    url?: string;
    location?: string;
    latitude?: number;
    longitude?: number;
}