import React, { useEffect, useState } from "react";
import type { Genre, PageResponse } from "../../types";
import { genreApi } from "../../api/genreApi";
import { Card } from "../../components/ui/Card";
import { Pagination } from "../../components/ui/Pagination";
import { handleApiError } from "../../api/errorHandler";

export const GenreList: React.FC = () => {
  const [genres, setGenres] = useState<PageResponse<Genre> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [page, setPage] = useState(0);
  const [size] = useState(20);

  const fetchGenres = async (pageNum: number) => {
    setIsLoading(true);
    setError("");
    try {
      const data = await genreApi.getGenreList({ page: pageNum, size, sort: "name,asc" });
      setGenres(data);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchGenres(page);
  }, [page]);

  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-bold">Genres</h1>

      {isLoading && !genres ? (
        <div className="text-center py-12">Loading genres...</div>
      ) : genres && genres.content.length > 0 ? (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
            {genres.content.map((genre) => (
              <Card key={genre.id} className="hover:shadow-md transition-shadow text-center">
                <p className="font-semibold text-lg">{genre.name}</p>
              </Card>
            ))}
          </div>

          {genres.totalPages > 1 && (
            <Pagination
              currentPage={page}
              totalPages={genres.totalPages}
              onPageChange={setPage}
              isLoading={isLoading}
            />
          )}
        </>
      ) : (
        <div className="text-center py-12">No genres found</div>
      )}
    </div>
  );
};
