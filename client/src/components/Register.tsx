import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import styles from '../styles/Register.module.css';
import {ErrorCode} from "../api/types";
import {useAuth} from "./contexts/AuthContext";
import {GoogleLogo} from "../constants/svg-logos";
import {useApi} from "./contexts/ApiContext";

const Register: React.FC = () => {
    const navigate = useNavigate();
    const auth = useAuth();
    const { api } = useApi();
    const [username, setUsername] = useState(auth.user?.username || '');
    const [email, setEmail] = useState(auth.user?.email || '');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            const res = await api.post('/auth/register', {username, email, password});
            if (res.status === 200) {
                const successRegistrationResponse: { emailVerified: boolean } = res.data;
                auth.refresh();

                if (!successRegistrationResponse.emailVerified) {
                    setSuccess('Registration successful! Please check your email to verify your account.');
                    setTimeout(() => navigate('/login'), 2000);
                } else {
                    setSuccess('Registration successful!');
                    setTimeout(() => navigate('/'), 1000);
                }
            }
        } catch (e: any) {
            const errorCode: ErrorCode = e.response?.data?.errorCode;
            if (errorCode == ErrorCode.RegistrationFailedAlreadyRegistered) {
                setError("User already registered");
            } else if (errorCode == ErrorCode.RegistrationFailedDuplicateUsername) {
                setError("Username already used by other user")
            } else {
                setError("Registration failed, try again soon")
            }
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        await api.post('/auth/register/cancel', null);
        setLoading(false);

        auth.refresh();
    }

    return (
        <div className={styles.registerContainer}>
            <h2 className={styles.title}>Register</h2>
            <form onSubmit={handleSubmit}>
                <div className={styles.formGroup}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        required
                        className={styles.input}
                    />
                </div>
                {!auth.user?.email &&
                    <div className={styles.formGroup}>
                        <input
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            required
                            className={styles.input}
                        />
                    </div>
                }
                <div className={styles.formGroup}>
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        required
                        className={styles.input}
                    />
                </div>
                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>{success}</div>}
                <button type="submit" disabled={loading} className={styles.submitButton}>
                    {loading ? 'Registering...' : 'Register'}
                </button>
            </form>
            <div className={styles.orLine}>
                <span className={styles.orText}>or</span>
            </div>
            {auth.user && !auth.user?.registered &&
                <button onClick={handleCancel} className={styles.submitButton} type="button">
                    Cancel
                </button>
            }
            {!auth.user &&
                <a href="http://localhost:9000/auth/oauth2/authorization/google" className={styles.googleButton}>
                    <GoogleLogo />
                    Register with Google
                </a>
            }
        </div>
    );
};

export default Register; 