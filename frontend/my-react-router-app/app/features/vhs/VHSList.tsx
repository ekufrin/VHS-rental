import React, { useEffect, useState } from "react";
import { Link } from "react-router";
import type { VHS, PageResponse } from "../../types";
import { vhsApi } from "../../api/vhsApi";
import { Card } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { handleApiError } from "../../api/errorHandler";
import { format } from "date-fns";
import { useAuthStore } from "../../stores/authStore";

export const VHSList: React.FC = () => {
  const [vhsList, setVhsList] = useState<PageResponse<VHS> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const { user } = useAuthStore();

  const fetchVHS = async () => {
    setIsLoading(true);
    setError("");
    try {
      const data = await vhsApi.getVHSList({ page: 0, size: 1000, sort: "title,asc" });
      setVhsList(data);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchVHS();
  }, []);

  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
  const joinUrl = (base: string, path: string) =>
    `${base.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`;

  const filteredVHS = vhsList?.content.filter((vhs) => {
    if (!user?.favoriteGenres || user.favoriteGenres.length === 0) {
      return true; 
    }
    return user.favoriteGenres.some((favGenre) => favGenre.id === vhs.genre.id);
  }) || [];

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">VHS Catalog</h1>
        <Link to="/vhs/add">
          <Button variant="primary">+ Add VHS</Button>
        </Link>
      </div>

      {isLoading && !vhsList ? (
        <div className="text-center py-12">Loading VHS titles...</div>
      ) : filteredVHS.length > 0 ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {filteredVHS.map((vhs) => (
              <Card key={vhs.id} className="hover:shadow-lg transition-shadow">
                {vhs.imageUrl && (
                  <img
                    src={joinUrl(API_BASE_URL, vhs.imageUrl)}
                    alt={vhs.title}
                    className="w-full max-h-32 object-contain rounded-lg mb-4"
                  />
                )}
                <h3 className="font-semibold text-lg mb-2">{vhs.title}</h3>
                <p className="text-sm text-gray-600 mb-2">{vhs.genre.name}</p>
                <p className="text-sm text-gray-500 mb-3">
                  {format(new Date(vhs.releaseDate), "yyyy")}
                </p>
                <div className="flex justify-between items-center">
                  <span className="text-blue-600 font-semibold">${vhs.rentalPrice.toFixed(2)} / day</span>
                  <Link to={`/vhs/${vhs.id}`}>
                    <Button variant="primary" size="sm">
                      View
                    </Button>
                  </Link>
                </div>
              </Card>
            ))}
          </div>
        </>
      ) : (
        <div className="text-center py-12">
          {user?.favoriteGenres && user.favoriteGenres.length > 0
            ? "No VHS found matching your favorite genres"
            : "No VHS titles found"}
        </div>
      )}
    </div>
  );
};
