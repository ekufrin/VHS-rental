import React, { useEffect, useState } from "react";
import type { Genre, PageResponse } from "../../types";
import { genreApi } from "../../api/genreApi";
import { userApi } from "../../api/userApi";
import { Button } from "../../components/ui/Button";
import { Card } from "../../components/ui/Card";
import { handleApiError } from "../../api/errorHandler";
import { useAuthStore } from "../../stores/authStore";

export const ProfileForm: React.FC = () => {
  const { user, setUser } = useAuthStore();
  const [genres, setGenres] = useState<Genre[]>([]);
  const [selectedGenres, setSelectedGenres] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string>("");
  const [success, setSuccess] = useState<string>("");

  useEffect(() => {
    const fetchGenres = async () => {
      setIsLoading(true);
      setError("");
      try {
        const data = await genreApi.getGenreList({ page: 0, size: 100, sort: "name,asc" });
        setGenres(data.content);
      } catch (err) {
        const apiError = handleApiError(err);
        setError(apiError.detail);
      } finally {
        setIsLoading(false);
      }
    };

    fetchGenres();
  }, []);

  useEffect(() => {
    if (user?.favoriteGenres) {
      setSelectedGenres(user.favoriteGenres.map((g) => g.id));
    }
  }, [user]);

  const handleToggleGenre = (genreId: string) => {
    setSelectedGenres((prev) =>
      prev.includes(genreId) ? prev.filter((id) => id !== genreId) : [...prev, genreId]
    );
  };

  const handleSave = async () => {
    setIsSaving(true);
    setError("");
    setSuccess("");
    try {
      const updatedUser = await userApi.updateFavoriteGenres(selectedGenres);
      setUser(updatedUser);
      setSuccess("Favorite genres updated successfully!");
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail || apiError.title);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <Card title="Profile Information">
        <div className="space-y-4">
          <div>
            <label className="text-sm text-gray-500">Name</label>
            <p className="text-lg font-medium">{user?.name || "N/A"}</p>
          </div>
          <div>
            <label className="text-sm text-gray-500">Email</label>
            <p className="text-lg font-medium">{user?.email || "N/A"}</p>
          </div>
        </div>
      </Card>

      <Card title="Favorite Genres">
        {error && <div className="mb-4 p-3 rounded bg-red-100 text-red-700 text-sm">{error}</div>}
        {success && <div className="mb-4 p-3 rounded bg-green-100 text-green-700 text-sm">{success}</div>}

        {isLoading ? (
          <div className="text-center py-4">Loading genres...</div>
        ) : (
          <>
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3 mb-4">
              {genres.map((genre) => (
                <label
                  key={genre.id}
                  className={`flex items-center p-3 rounded border cursor-pointer transition-colors ${
                    selectedGenres.includes(genre.id)
                      ? "border-blue-600 bg-blue-50"
                      : "border-gray-300 hover:border-blue-400"
                  }`}
                >
                  <input
                    type="checkbox"
                    checked={selectedGenres.includes(genre.id)}
                    onChange={() => handleToggleGenre(genre.id)}
                    className="mr-2"
                  />
                  <span className="text-sm">{genre.name}</span>
                </label>
              ))}
            </div>
            <Button onClick={handleSave} isLoading={isSaving} variant="primary">
              Save Favorite Genres
            </Button>
          </>
        )}
      </Card>
    </div>
  );
};
