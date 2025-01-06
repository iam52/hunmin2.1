import React from 'react';

const Logout = () => {
    const handleLogout = async () => {
        try {
            const response = await fetch('/api/members/logout', {
                method: 'POST',
                credentials: 'include', // 쿠키 전송을 위해 필요
            });

            if (!response.ok) {
                throw new Error('Logout failed');
            }

            // CustomLogoutFilter 처리 후 로컬 스토리지 클리어
            localStorage.removeItem('accessToken');
            localStorage.removeItem('memberId');
            localStorage.removeItem('role');
            localStorage.removeItem('nickname');
            window.location.href = '/login';
        } catch (err) {
            console.error('로그아웃 실패:', err);
        }
    };

    return (
        <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-4 py-2 rounded"
        >
            로그아웃
        </button>
    );
};

export default Logout;