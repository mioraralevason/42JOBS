import { useState, type FormEvent, type ChangeEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Popup from './Popup';
import '../styles/Login.css';
import config from '../config';

interface ErrorState {
  username: string;
  password: string;
  api?: string;
}

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [remember, setRemember] = useState(false);
  const [error, setError] = useState<ErrorState>({ username: '', password: '' });
  const [success, setSuccess] = useState(false);
  const [popupMessage, setPopupMessage] = useState('');
  const [popupType, setPopupType] = useState<'success' | 'error' | null>(null);

  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Reset des erreurs et popup avant validation
    setPopupType(null);
    setPopupMessage('');
    setError({ username: '', password: '' });

    let valid = true;
    const errors: ErrorState = { username: '', password: '' };

    if (!username) {
        errors.username = 'Username is required';
        valid = false;
    }
    if (!password) {
        errors.password = 'Password is required';
        valid = false;
    }

    setError(errors);
    if (!valid) return;

    try {
        const response = await fetch(`${config.API_BASE_URL}/jobs/companies/authenticate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ login: username, password })
        });

        const data = await response.json();

        if (response.ok && data.status) {
        setSuccess(true);
        setError({ username: '', password: '', api: '' });
        setPopupType('success');
        setPopupMessage('Login successful! Redirecting to dashboard...');
        setTimeout(() => navigate('/dashboard'), 2000);
        } else {
        const msg = data.message || 'Authentication failed';
        setError({ username: '', password: '', api: msg });
        setPopupType('error');
        setPopupMessage(msg);
        }
    } catch {
        const msg = 'Unable to connect to server';
        setError({ username: '', password: '', api: msg });
        setPopupType('error');
        setPopupMessage(msg);
    }
    };


  return (
      <div className="login-container">
        {popupType && <Popup type={popupType} message={popupMessage} />}
      <div className="login-card">

        {/* Popup */}

        <div className="login-header">
          <div className="logo-icon">⚡</div>
          <h2>Sign In</h2>
          <p>Access your account</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <div className="form-row">
            <div className={`form-group ${error.username ? 'error' : ''}`}>
              <div className="input-wrapper">
                <input
                  type="text"
                  id="username"
                  value={username}
                  onChange={(e: ChangeEvent<HTMLInputElement>) => setUsername(e.target.value)}
                  required
                />
                <label htmlFor="username">Username</label>
                <span className="input-line"></span>
              </div>
            </div>

            <div className={`form-group ${error.password ? 'error' : ''}`}>
              <div className="input-wrapper password-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  value={password}
                  onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                  required
                />
                <label htmlFor="password">Password</label>
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  <span className={`toggle-icon ${showPassword ? 'show-password' : ''}`}></span>
                </button>
                <span className="input-line"></span>
              </div>
            </div>
          </div>

          <div className="form-options">
            <div className="remember-wrapper">
              <input
                type="checkbox"
                id="remember"
                checked={remember}
                onChange={() => setRemember(!remember)}
              />
              <label htmlFor="remember" className="checkbox-label">
                <span className="custom-checkbox"></span> Keep me signed in
              </label>
            </div>
            <a href="#" className="forgot-password">Forgot password?</a>
          </div>

          <button type="submit" className="login-btn btn">
            <span className="btn-text">Sign In</span>
            <span className="btn-loader"></span>
            <span className="btn-glow"></span>
          </button>
        </form>

        <div className="divider"><span>or</span></div>

        <div className="social-login">
          <button type="button" className="social-btn google-btn">
            SIGN IN WITH INTRA
          </button>
        </div>

        <div className="signup-link">
          <p>New here? <Link to="/register">Create an account</Link></p>
        </div>
      </div>
    </div>
  );
}
