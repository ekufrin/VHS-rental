import React, { useEffect, useState } from "react";
import type { Review, PageResponse } from "../../types";
import { reviewApi } from "../../api/reviewApi";
import { Card } from "../../components/ui/Card";
import { handleApiError } from "../../api/errorHandler";

export const ReviewList: React.FC<{ vhsId: string }> = ({ vhsId }) => {
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    fetchReviews();
  }, [vhsId]);

  const fetchReviews = async () => {
    setIsLoading(true);
    setError("");
    try {
      const data = await reviewApi.getReviewsByVHS(vhsId, { page: 0, size: 50, sort: "id,desc" });
      setReviews(data.content);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold">Reviews</h2>

      <div className="space-y-4">
        {isLoading ? (
          <div className="text-center py-6">Loading reviews...</div>
        ) : reviews.length > 0 ? (
          reviews.map((review) => (
            <Card key={review.id}>
              <div className="space-y-2">
                <div className="flex justify-between items-start">
                  <span className="font-semibold">{review.user.email}</span>
                  <span className="text-yellow-600 font-semibold">{review.rating} ★</span>
                </div>
                {review.comment && <p className="text-gray-700">{review.comment}</p>}
              </div>
            </Card>
          ))
        ) : (
          <div className="text-center py-6 text-gray-500">No reviews yet</div>
        )}
      </div>
    </div>
  );
};
