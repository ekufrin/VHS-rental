import React, { useEffect, useState } from "react";
import type { Rental, PageResponse } from "../../types";
import { rentalApi } from "../../api/rentalApi";
import { Card } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { Pagination } from "../../components/ui/Pagination";
import { handleApiError } from "../../api/errorHandler";
import { format } from "date-fns";
import { useAuthStore } from "../../stores/authStore";

export const RentalList: React.FC = () => {
  const [rentals, setRentals] = useState<PageResponse<Rental> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const { user } = useAuthStore();

  const fetchRentals = async (pageNum: number) => {
    setIsLoading(true);
    setError("");
    try {
      const data = await rentalApi.getRentalList({ page: pageNum, size, sort: "rentalDate,desc" });
      setRentals(data);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRentals(page);
  }, [page]);

  const handleReturnVHS = async (rentalId: string) => {
    try {
      await rentalApi.finishRental(rentalId);
      fetchRentals(page);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    }
  };

  const getReturnStatus = (rental: Rental) => {
    if (!rental.returnDate) return "Active";
    const returnDate = new Date(rental.returnDate);
    const dueDate = new Date(rental.dueDate);
    return returnDate > dueDate ? "DELAYED" : "Returned";
  };

  const getStatusColor = (rental: Rental) => {
    const status = getReturnStatus(rental);
    if (status === "DELAYED") return "text-red-600";
    if (status === "Active") return "text-blue-600";
    return "text-green-600";
  };

  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  const filteredContent = rentals?.content.filter((rental) =>
    user?.email ? rental.user.email === user.email : false
  );

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-bold">My Rentals</h1>

      {isLoading && !rentals ? (
        <div className="text-center py-12">Loading rentals...</div>
      ) : rentals && filteredContent && filteredContent.length > 0 ? (
        <>
          <div className="space-y-4">
            {filteredContent.map((rental) => (
              <Card key={rental.id} className="hover:shadow-md transition-shadow">
                <div className="grid grid-cols-1 md:grid-cols-6 gap-4">
                  <div>
                    <label className="text-sm text-gray-500">VHS Title</label>
                    <p className="font-semibold">{rental.vhs.title}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Rental Date</label>
                    <p>{format(new Date(rental.rentalDate), "MMM dd, yyyy")}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Due Date</label>
                    <p className="font-semibold">{format(new Date(rental.dueDate), "MMM dd, yyyy")}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Return Date</label>
                    <p className="font-semibold">{rental.returnDate ? format(new Date(rental.returnDate), "MMM dd, yyyy") : "-"}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Status</label>
                    <p className={`font-semibold ${getStatusColor(rental)}`}>{getReturnStatus(rental)}</p>
                  </div>
                  <div>
                    <label className="text-sm text-gray-500">Price</label>
                    <p className="font-semibold">{rental.price != null ? `$${rental.price.toFixed(2)}` : "N/A"}</p>
                  </div>
                </div>
                {!rental.returnDate && (
                  <div className="mt-4 flex gap-2">
                    <Button
                      variant="primary"
                      size="sm"
                      onClick={() => handleReturnVHS(rental.id)}
                    >
                      Return VHS
                    </Button>
                  </div>
                )}
              </Card>
            ))}
          </div>

          {rentals.totalPages > 1 && (
            <Pagination
              currentPage={page}
              totalPages={rentals.totalPages}
              onPageChange={setPage}
              isLoading={isLoading}
            />
          )}
        </>
      ) : (
        <div className="text-center py-12">No rentals found</div>
      )}
    </div>
  );
};
