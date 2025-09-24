import { useState, useEffect, type FormEvent, type ChangeEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Register.css';
import config from '../config';

interface ErrorState {
  companyName: string;
  responsibleName: string;
  responsiblePosition: string;
  companyAddress: string;
  email: string;
  telephone: string;
  password: string;
  confirmPassword: string;
}

export default function Register() {
  const [formData, setFormData] = useState({
    companyName: '',
    responsibleName: '',
    responsiblePosition: '',
    companyAddress: '',
    email: '',
    telephone: '',
    password: '',
    confirmPassword: '',
  });

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState<ErrorState>({
    companyName: '',
    responsibleName: '',
    responsiblePosition: '',
    companyAddress: '',
    email: '',
    telephone: '',
    password: '',
    confirmPassword: '',
  });

  const [success, setSuccess] = useState(false);
  const [countdown, setCountdown] = useState(5);
  const [apiData, setApiData] = useState<{ name: string; email: string; login: string } | null>(null);

  const navigate = useNavigate();

  const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));

    // validation confirmPassword
    if (name === 'confirmPassword') {
      setError(prev => ({
        ...prev,
        confirmPassword:
          value === ''
            ? 'Confirm password is required'
            : value !== formData.password
            ? 'Passwords do not match'
            : '',
      }));
    }

    if (name === 'password' && formData.confirmPassword !== '') {
      setError(prev => ({
        ...prev,
        confirmPassword:
          value === formData.confirmPassword ? '' : 'Passwords do not match',
      }));
    }
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    let valid = true;
    const errors: ErrorState = {
      companyName: '',
      responsibleName: '',
      responsiblePosition: '',
      companyAddress: '',
      email: '',
      telephone: '',
      password: '',
      confirmPassword: '',
    };

    // validations
    if (!formData.companyName) { errors.companyName = 'Company name is required'; valid = false; }
    if (!formData.responsibleName) { errors.responsibleName = 'Responsible name is required'; valid = false; }
    if (!formData.responsiblePosition) { errors.responsiblePosition = 'Responsible position is required'; valid = false; }
    if (!formData.companyAddress) { errors.companyAddress = 'Company address is required'; valid = false; }
    if (!formData.email) { errors.email = 'Email is required'; valid = false; }
    else if (!/\S+@\S+\.\S+/.test(formData.email)) { errors.email = 'Invalid email format'; valid = false; }
    if (!formData.telephone) { errors.telephone = 'Telephone is required'; valid = false; }
    if (!formData.password) { errors.password = 'Password is required'; valid = false; }
    else if (formData.password.length < 8) { errors.password = 'Password must be at least 8 characters'; valid = false; }
    if (!formData.confirmPassword) { errors.confirmPassword = 'Confirm password is required'; valid = false; }
    else if (formData.password !== formData.confirmPassword) { errors.confirmPassword = 'Passwords do not match'; valid = false; }

    setError(errors);

    if (!valid) return;

    try {
      const response = await fetch(`${config.API_BASE_URL}/jobs/companies`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.companyName,
          responsable: formData.responsibleName,
          poste: formData.responsiblePosition,
          telephone: formData.telephone,
          email: formData.email,
          adresse: formData.companyAddress,
          password: formData.password,
        }),
      });

      const data = await response.json();

      if (response.ok && data.status) {
        setApiData({
          name: data.data.name,
          email: data.data.email,
          login: data.data.login
        });
        setSuccess(true);
        setCountdown(5);
      } else {
        alert(data.message || 'Error creating company');
      }
    } catch (err) {
      alert('Unable to connect to server');
    }
  };

  // countdown effect
  useEffect(() => {
    if (!success) return;
    // if (countdown <= -1) {
    //   navigate('/login');
    //   return;
    // }
    const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
    return () => clearTimeout(timer);
  }, [success, countdown, navigate]);

  return (
    <div className="register-container">
      <div className="register-card">
        {success && apiData ? (
          <div className="success-message show">
            <div className="success-icon">✓</div>
            <h3>Account Created!</h3>
            <p>
              A company representative will send your login credentials to the email you provided: <strong>{apiData.email}</strong>.
            </p>
            <p>
              You will use it to log in. Please make sure to re-register if the email you provided is incorrect or does not match the company's email: <strong>{apiData.name}</strong>.
            </p>
            {/* <p>Redirecting in {countdown} second{countdown > 1 ? 's' : ''}...</p> */}
            <button
              className="register-btn btn"
              onClick={() => navigate('/login')}
            >
              Go to Login
            </button>
          </div>
        ) : (
          <>
            <div className="register-header">
              <div className="logo-icon">⚡</div>
              <h2>Create Account</h2>
              <p>Register your company account</p>
            </div>

            <form className="register-form" onSubmit={handleSubmit} noValidate>
              {/* Form rows and input fields as before */}
              {/* Company Name & Address */}
              <div className="form-row">
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="text" id="companyName" name="companyName" value={formData.companyName} onChange={handleInputChange} required/>
                    <label htmlFor="companyName">Company Name</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.companyName ? 'show' : ''}`}>{error.companyName}</span>
                </div>
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="text" id="companyAddress" name="companyAddress" value={formData.companyAddress} onChange={handleInputChange} required/>
                    <label htmlFor="companyAddress">Company Address</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.companyAddress ? 'show' : ''}`}>{error.companyAddress}</span>
                </div>
              </div>

              {/* Responsible Position & Name */}
              <div className="form-row">
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="text" id="responsiblePosition" name="responsiblePosition" value={formData.responsiblePosition} onChange={handleInputChange} required/>
                    <label htmlFor="responsiblePosition">Responsible Position</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.responsiblePosition ? 'show' : ''}`}>{error.responsiblePosition}</span>
                </div>
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="text" id="responsibleName" name="responsibleName" value={formData.responsibleName} onChange={handleInputChange} required/>
                    <label htmlFor="responsibleName">Responsible Name</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.responsibleName ? 'show' : ''}`}>{error.responsibleName}</span>
                </div>
              </div>

              {/* Email & Telephone */}
              <div className="form-row">
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="email" id="email" name="email" value={formData.email} onChange={handleInputChange} required/>
                    <label htmlFor="email">Professional Email</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.email ? 'show' : ''}`}>{error.email}</span>
                </div>
                <div className="form-group">
                  <div className="input-wrapper">
                    <input type="tel" id="telephone" name="telephone" value={formData.telephone} onChange={handleInputChange} required/>
                    <label htmlFor="telephone">Telephone</label>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.telephone ? 'show' : ''}`}>{error.telephone}</span>
                </div>
              </div>

              {/* Password & Confirm Password */}
              <div className="form-row">
                <div className="form-group">
                  <div className="input-wrapper password-wrapper">
                    <input type={showPassword ? 'text' : 'password'} id="password" name="password" value={formData.password} onChange={handleInputChange} required/>
                    <label htmlFor="password">Password</label>
                    <button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)}>
                      <span className={`toggle-icon ${showPassword ? 'show-password' : ''}`}></span>
                    </button>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.password ? 'show' : ''}`}>{error.password}</span>
                </div>
                <div className="form-group">
                  <div className="input-wrapper password-wrapper">
                    <input type={showConfirmPassword ? 'text' : 'password'} id="confirmPassword" name="confirmPassword" value={formData.confirmPassword} onChange={handleInputChange} required/>
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <button type="button" className="password-toggle" onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
                      <span className={`toggle-icon ${showConfirmPassword ? 'show-password' : ''}`}></span>
                    </button>
                    <span className="input-line"></span>
                  </div>
                  <span className={`error-message ${error.confirmPassword ? 'show' : ''}`}>{error.confirmPassword}</span>
                </div>
              </div>

              <button type="submit" className="register-btn btn">
                <span className="btn-text">Create Account</span>
                <span className="btn-loader"></span>
                <span className="btn-glow"></span>
              </button>
            </form>
            <div className="signup-link">
              <p>Already have an account? <Link to="/login">Log in</Link></p>
            </div>
          </>
          
        )}
      </div>
    </div>
  );
}
