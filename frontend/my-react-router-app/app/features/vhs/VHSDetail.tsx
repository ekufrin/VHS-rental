import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import type { VHS } from "../../types";
import { vhsApi } from "../../api/vhsApi";
import { Card } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { handleApiError } from "../../api/errorHandler";
import { format } from "date-fns";
import { rentalApi } from "../../api/rentalApi";
import { useAuthStore } from "../../stores/authStore";
import { ReviewList } from "../review/ReviewList";

export const VHSDetail: React.FC = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [vhs, setVhs] = useState<VHS | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [rentError, setRentError] = useState<string>("");
  const [isRenting, setIsRenting] = useState(false);
  const [dueDate, setDueDate] = useState<string>("");
  const { isAuthenticated } = useAuthStore();

  useEffect(() => {
    const fetchVHS = async () => {
      if (!id) return;
      setIsLoading(true);
      setError("");
      try {
        const data = await vhsApi.getVHSDetail(id);
        setVhs(data);
      } catch (err) {
        const apiError = handleApiError(err);
        setError(apiError.detail);
      } finally {
        setIsLoading(false);
      }
    };

    fetchVHS();
  }, [id]);

  if (isLoading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;
  if (!vhs) return <div className="text-center py-8">VHS not found</div>;

  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
  const joinUrl = (base: string, path: string) =>
    `${base.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`;

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const minDate = format(new Date(today.getTime() + 24 * 60 * 60 * 1000), "yyyy-MM-dd");

  const handleRent = async () => {
    if (!vhs) return;
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    setRentError("");
    if (!dueDate) {
      setRentError("Please select a due date.");
      return;
    }

    const selected = new Date(dueDate);
    selected.setHours(0, 0, 0, 0);
    if (selected <= today) {
      setRentError("Due date must be in the future.");
      return;
    }

    const dueDateIso = `${dueDate}T12:00:00Z`;
    setIsRenting(true);
    try {
      await rentalApi.createRental({ vhsId: vhs.id, dueDate: dueDateIso });
      navigate("/rentals");
    } catch (err) {
      const apiError = handleApiError(err);
      setRentError(apiError.detail || "Unable to create rental.");
    } finally {
      setIsRenting(false);
    }
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
      <div className="md:col-span-1">
        {vhs.imageUrl && (
          <img
            src={joinUrl(API_BASE_URL, vhs.imageUrl)}
            alt={vhs.title}
            className="w-full max-h-100 object-contain rounded-lg shadow-md"
          />
        )}
      </div>

      <div className="md:col-span-2">
        <Card title={vhs.title}>
          <div className="space-y-4">
            <div>
              <label className="text-sm text-gray-500">Genre</label>
              <p className="text-lg font-medium">{vhs.genre.name}</p>
            </div>

            <div>
              <label className="text-sm text-gray-500">Release Date</label>
              <p className="text-lg font-medium">
                {format(new Date(vhs.releaseDate), "MMM dd, yyyy")}
              </p>
            </div>

            <div>
              <label className="text-sm text-gray-500">Rental Price</label>
              <p className="text-lg font-medium">${vhs.rentalPrice.toFixed(2)} / day</p>
            </div>

            <div>
              <label className="text-sm text-gray-500">Due Date</label>
              <input
                type="date"
                className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                min={minDate}
              />
            </div>

            <div>
              <label className="text-sm text-gray-500">Stock Level</label>
              <p className={`text-lg font-medium ${vhs.stockLevel > 0 ? "text-green-600" : "text-red-600"}`}>
                {vhs.stockLevel > 0 ? `${vhs.stockLevel}` : "Out of stock"}
              </p>
            </div>

            <div>
              <label className="text-sm text-gray-500">Status</label>
              <p className="text-lg font-medium capitalize">{vhs.status}</p>
            </div>

            {rentError && (
              <div className="p-3 rounded bg-red-100 text-red-700 text-sm">{rentError}</div>
            )}

            <Button
              variant="primary"
              size="lg"
              disabled={vhs.stockLevel === 0 || isRenting}
              className="w-full mt-6"
              onClick={handleRent}
            >
              {isRenting ? "Processing..." : "Rent Now"}
            </Button>
          </div>
        </Card>
      </div>

      <div className="md:col-span-3">
        <ReviewList vhsId={vhs.id} />
      </div>
    </div>
  );
};
