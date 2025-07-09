export interface ErrorResponse {
  message: string,
  errorCode: ErrorCode,
}

export enum ErrorCode {
    Unknown = -1,

    UserNotFound = 100,

    SessionNotFound = 200,
    SessionAlreadyRevoked = 201,
    SessionDoesNotBelongToUser = 202,

    RefreshIsTooFrequent = 300,

    RegistrationFailedDuplicateUsername = 400,
    RegistrationFailedAlreadyRegistered = 401,

    EmailVerificationTokenNotFound = 500,
    EmailVerificationAlreadyVerified = 501,

    InvalidCredentials = 600,
    AuthenticationFailed = 601,
    TokenGenerationFailed = 602,
}

export interface RegisterRequest {
  username: string,
  email: string,
  password: string,
}

export interface User {
    username: string,
    email: string,
    emailVerified: boolean,
    authorities: string[],
    registered: boolean,
}