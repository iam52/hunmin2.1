import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import BoardListPage from './pages/BoardListPage';

const PrivateRoute = ({ children }) => {
    const accessToken = localStorage.getItem('accessToken');
    return accessToken ? children : <Navigate to="/login" />;
};

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route
                    path="/board"
                    element={
                        <PrivateRoute>
                            <BoardListPage />
                        </PrivateRoute>
                    }
                />
                <Route path="/" element={<Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;