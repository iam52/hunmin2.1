import React, { useState, useEffect } from 'react';
import Logout from '../auth/Logout';

const BoardList = () => {
    const [boards, setBoards] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchBoards();
    }, [currentPage]);

    const fetchBoards = async () => {
        try {
            const accessToken = localStorage.getItem('accessToken');
            const response = await fetch(`/api/board?page=${currentPage}&size=5`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    window.location.href = '/login';
                    return;
                }
                throw new Error('Failed to fetch boards');
            }

            const data = await response.json();
            setBoards(data.content);
            setTotalPages(data.totalPages);
        } catch (err) {
            setError('게시글을 불러오는데 실패했습니다.');
        }
    };

    const handleDeleteBoard = async (boardId) => {
        try {
            const accessToken = localStorage.getItem('accessToken');
            const response = await fetch(`/api/board/${boardId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                }
            });

            if (!response.ok) {
                throw new Error('Failed to delete board');
            }

            fetchBoards(); // 삭제 후 목록 새로고침
        } catch (err) {
            setError('게시글 삭제에 실패했습니다.');
        }
    };

    return (
        <div className="p-4">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">게시판</h1>
                <div className="flex items-center gap-4">
          <span className="text-gray-600">
            {localStorage.getItem('nickname')}님 환영합니다
          </span>
                    <Logout />
                </div>
            </div>

            {error && <p className="text-red-500 mb-4">{error}</p>}

            <div className="space-y-4">
                {boards.map((board) => (
                    <div key={board.boardId} className="border p-4 rounded">
                        <h2 className="text-xl font-semibold">{board.title}</h2>
                        <p className="mt-2">{board.content}</p>
                        <div className="mt-2 text-sm text-gray-500">
                            <span>작성자: {board.memberNickname}</span>
                            <span className="ml-4">
                작성일: {new Date(board.createdAt).toLocaleDateString()}
              </span>
                        </div>
                        {board.memberId === parseInt(localStorage.getItem('memberId')) && (
                            <div className="mt-2">
                                <button
                                    onClick={() => handleDeleteBoard(board.boardId)}
                                    className="text-red-500 hover:text-red-700"
                                >
                                    삭제
                                </button>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <div className="mt-4 flex justify-center gap-2">
                {[...Array(totalPages)].map((_, i) => (
                    <button
                        key={i}
                        onClick={() => setCurrentPage(i + 1)}
                        className={`px-3 py-1 rounded ${
                            currentPage === i + 1 ? 'bg-blue-500 text-white' : 'bg-gray-200'
                        }`}
                    >
                        {i + 1}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default BoardList;