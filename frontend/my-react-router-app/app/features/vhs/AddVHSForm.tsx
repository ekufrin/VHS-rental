import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import type { Genre, PageResponse, VHSStatus } from "../../types";
import { vhsApi } from "../../api/vhsApi";
import { genreApi } from "../../api/genreApi";
import { Button } from "../../components/ui/Button";
import { Card } from "../../components/ui/Card";
import { Input } from "../../components/ui/Input";
import { handleApiError } from "../../api/errorHandler";

const addVHSSchema = z.object({
  title: z.string().nonempty("Title is required"),
  releaseDate: z.string().nonempty("Release date is required"),
  genreId: z.string().nonempty("Genre is required"),
  rentalPrice: z.coerce.number().positive("Rental price must be positive"),
  stockLevel: z.coerce.number().nonnegative("Stock level must be non-negative"),
  status: z.string().nonempty("Status is required"),
});

type AddVHSFormData = z.infer<typeof addVHSSchema>;

export const AddVHSForm: React.FC = () => {
  const navigate = useNavigate();
  const [genres, setGenres] = useState<Genre[]>([]);
  const [isLoadingGenres, setIsLoadingGenres] = useState(true);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<AddVHSFormData>({
    resolver: zodResolver(addVHSSchema),
  });

  useEffect(() => {
    const fetchGenres = async () => {
      setIsLoadingGenres(true);
      try {
        const data: PageResponse<Genre> = await genreApi.getGenreList({
          page: 0,
          size: 100,
          sort: "name,asc",
        });
        setGenres(data.content);
      } catch (err) {
        const apiError = handleApiError(err);
        setError(apiError.detail || "Failed to load genres");
      } finally {
        setIsLoadingGenres(false);
      }
    };

    fetchGenres();
  }, []);

  const onSubmit = async (data: AddVHSFormData) => {
    setError("");
    setIsSubmitting(true);

    try {
      const formData = new FormData();
      formData.append("title", data.title);
      formData.append("releaseDate", `${data.releaseDate}T12:00:00Z`);
      formData.append("genreId", data.genreId);
      formData.append("rentalPrice", data.rentalPrice.toString());
      formData.append("stockLevel", data.stockLevel.toString());
      formData.append("status", data.status);
      
      if (selectedFile) {
        formData.append("image", selectedFile);
      }

      await vhsApi.createVHS(formData);
      navigate("/vhs");
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail || apiError.title || "Failed to create VHS");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFile(e.target.files[0]);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Card title="Add New VHS Title">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {error && (
            <div className="p-4 bg-red-100 text-red-700 rounded-lg">{error}</div>
          )}

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Title</label>
            <input
              {...register("title")}
              type="text"
              placeholder="VHS Title"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.title && <p className="text-red-600 text-sm mt-1">{errors.title.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Release Date</label>
            <input
              {...register("releaseDate")}
              type="date"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.releaseDate && <p className="text-red-600 text-sm mt-1">{errors.releaseDate.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Genre</label>
            <select
              {...register("genreId")}
              disabled={isLoadingGenres}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select a genre...</option>
              {genres.map((genre) => (
                <option key={genre.id} value={genre.id}>
                  {genre.name}
                </option>
              ))}
            </select>
            {errors.genreId && <p className="text-red-600 text-sm mt-1">{errors.genreId.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Rental Price ($ per day)</label>
            <input
              {...register("rentalPrice")}
              type="number"
              step="0.01"
              min="0"
              placeholder="5.99"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.rentalPrice && <p className="text-red-600 text-sm mt-1">{errors.rentalPrice.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Level</label>
            <input
              {...register("stockLevel")}
              type="number"
              min="0"
              placeholder="10"
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {errors.stockLevel && <p className="text-red-600 text-sm mt-1">{errors.stockLevel.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
            <select
              {...register("status")}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select a status...</option>
              <option value="AVAILABLE">Available</option>
              <option value="OUT_OF_STOCK">Out of Stock</option>
              <option value="DAMAGED">Damaged</option>
              <option value="LOST">Lost</option>
            </select>
            {errors.status && <p className="text-red-600 text-sm mt-1">{errors.status.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image (Optional)</label>
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            {selectedFile && <p className="text-sm text-gray-600 mt-1">{selectedFile.name}</p>}
          </div>

          <div className="flex gap-4 pt-4">
            <Button type="submit" isLoading={isSubmitting} className="flex-1">
              Add VHS
            </Button>
            <Button
              type="button"
              onClick={() => navigate("/vhs")}
              className="flex-1 bg-gray-600 hover:bg-gray-700"
            >
              Cancel
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
