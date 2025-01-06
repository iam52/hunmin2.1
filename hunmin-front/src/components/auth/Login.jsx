import React, { useState } from 'react';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch('/api/members/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
                credentials: 'include', // 쿠키를 받기 위해 필요
            });

            if (!response.ok) {
                throw new Error('Login failed');
            }

            const data = await response.json();
            // CustomLoginFilter의 응답으로 받은 데이터 저장
            localStorage.setItem('accessToken', data.token);
            localStorage.setItem('memberId', data.memberId);
            localStorage.setItem('role', data.role);
            localStorage.setItem('nickname', data.nickname);
            window.location.href = '/board';
        } catch (err) {
            setError('로그인에 실패했습니다.');
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center">
            <div className="max-w-md w-full p-6">
                <h2 className="text-2xl font-bold mb-6">로그인</h2>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                <form onSubmit={handleLogin}>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="이메일"
                        className="w-full mb-4 p-2 border rounded"
                        required
                    />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="비밀번호"
                        className="w-full mb-6 p-2 border rounded"
                        required
                    />
                    <button
                        type="submit"
                        className="w-full bg-blue-500 text-white p-2 rounded"
                    >
                        로그인
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;