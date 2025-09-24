import { useEffect, useState } from 'react';
import '../styles/Popup.css';

interface PopupProps {
  type: 'success' | 'error';
  message: string;
  duration?: number; // durée avant disparition en ms
}

export default function Popup({ type, message, duration = 3000 }: PopupProps) {
  const [show, setShow] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setShow(false), duration);
    return () => clearTimeout(timer);
  }, [duration]);

  if (!show) return null;

  return (
    <div className={`popup ${type}`}>
      <span className="icon">{type === 'success' ? '✓' : '✗'}</span>
      <span className="message">{message}</span>
    </div>
  );
}
