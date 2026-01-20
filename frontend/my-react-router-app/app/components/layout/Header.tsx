import React from "react";
import { Link, useNavigate } from "react-router";
import { useAuthStore } from "../../stores/authStore";
import { authApi } from "../../api/authApi";

export const Header: React.FC = () => {
  const { isAuthenticated, user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } finally {
      logout();
      navigate("/login");
    }
  };

  return (
    <header className="bg-white shadow-sm">
      <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <Link to="/" className="text-2xl font-bold text-blue-600">
          VHS Rental
        </Link>

        <nav className="flex gap-6 items-center">
          <Link to="/vhs" className="text-gray-700 hover:text-blue-600">
            VHS Catalog
          </Link>
          <Link to="/rentals" className="text-gray-700 hover:text-blue-600">
            My Rentals
          </Link>
          <Link to="/reviews" className="text-gray-700 hover:text-blue-600">
            My Reviews
          </Link>
          <Link to="/genres" className="text-gray-700 hover:text-blue-600">
            Genres
          </Link>

          {isAuthenticated ? (
            <div className="flex gap-4 items-center">
              <span className="text-gray-700">{user?.email}</span>
              <Link to="/profile" className="text-gray-700 hover:text-blue-600">
                Profile
              </Link>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="flex gap-4">
              <Link
                to="/login"
                className="px-4 py-2 text-blue-600 hover:text-blue-700 border border-blue-600 rounded-lg"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                Register
              </Link>
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};
