import React, { useEffect, useState } from "react";
import type { Rental, Review, PageResponse } from "../../types";
import { rentalApi } from "../../api/rentalApi";
import { reviewApi } from "../../api/reviewApi";
import { Card } from "../../components/ui/Card";
import { Button } from "../../components/ui/Button";
import { handleApiError } from "../../api/errorHandler";
import { format } from "date-fns";
import { useAuthStore } from "../../stores/authStore";

export const MyReviews: React.FC = () => {
  const [rentals, setRentals] = useState<Rental[]>([]);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>("");
  const [reviewError, setReviewError] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [activeRentalId, setActiveRentalId] = useState<string | null>(null);
  const [rating, setRating] = useState<number>(5);
  const [comment, setComment] = useState<string>("");
  const [editingReviewId, setEditingReviewId] = useState<string | null>(null);
  const [editRating, setEditRating] = useState<number>(5);
  const [editComment, setEditComment] = useState<string>("");
  const { user } = useAuthStore();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setIsLoading(true);
    setError("");
    try {
      const rentalData = await rentalApi.getRentalList({ page: 0, size: 1000, sort: "rentalDate,desc" });
      const userRentals = rentalData.content.filter((rental) =>
        user?.email ? rental.user.email === user.email : false
      );
      setRentals(userRentals);

      const allReviews: Review[] = [];
      for (const rental of userRentals) {
        try {
          const reviewData = await reviewApi.getReviewsByVHS(rental.vhs.id, { page: 0, size: 100, sort: "id,desc" });
          const userReviewsForVHS = reviewData.content.filter((review) => review.user.email === user?.email);
          allReviews.push(...userReviewsForVHS);
        } catch (err) {
        }
      }
      setReviews(allReviews);
    } catch (err) {
      const apiError = handleApiError(err);
      setError(apiError.detail);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmitReview = async (rentalId: string) => {
    setIsSubmitting(true);
    setReviewError("");
    try {
      await reviewApi.createReview({ rentalId, rating, comment: comment || undefined });
      setActiveRentalId(null);
      setRating(5);
      setComment("");
      fetchData();
    } catch (err) {
      const apiError = handleApiError(err);
      setReviewError(apiError.detail || apiError.title);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUpdateReview = async (reviewId: string) => {
    setIsSubmitting(true);
    setReviewError("");
    try {
      await reviewApi.updateReview(reviewId, { rating: editRating, comment: editComment || undefined });
      setEditingReviewId(null);
      setEditRating(5);
      setEditComment("");
      fetchData();
    } catch (err) {
      const apiError = handleApiError(err);
      setReviewError(apiError.detail || apiError.title);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteReview = async (reviewId: string) => {
    if (!confirm("Are you sure you want to delete this review?")) return;
    
    setIsSubmitting(true);
    setReviewError("");
    try {
      await reviewApi.deleteReview(reviewId);
      fetchData();
    } catch (err) {
      const apiError = handleApiError(err);
      setReviewError(apiError.detail || apiError.title);
    } finally {
      setIsSubmitting(false);
    }
  };

  const startEditReview = (review: Review) => {
    setEditingReviewId(review.id);
    setEditRating(review.rating);
    setEditComment(review.comment || "");
  };

  if (isLoading) return <div className="text-center py-8">Loading...</div>;
  if (error) return <div className="text-center py-8 text-red-600">{error}</div>;

  const reviewedRentalIds = new Set(reviews.map((review) => review.vhs.id));
  const pendingReviews = rentals.filter(
    (rental) => rental.returnDate && !reviewedRentalIds.has(rental.vhs.id)
  );

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-bold">My Reviews</h1>

      <div>
        <h2 className="text-2xl font-semibold mb-4">Waiting to Review</h2>
        {pendingReviews.length > 0 ? (
          <div className="space-y-4">
            {pendingReviews.map((rental) => (
              <Card key={rental.id} className="hover:shadow-md transition-shadow">
                <div className="space-y-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <h3 className="font-semibold text-lg">{rental.vhs.title}</h3>
                      <p className="text-sm text-gray-600">{rental.vhs.genre.name}</p>
                      <p className="text-sm text-gray-500">
                        Returned: {format(new Date(rental.returnDate!), "MMM dd, yyyy")}
                      </p>
                    </div>
                    {activeRentalId !== rental.id && (
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => setActiveRentalId(rental.id)}
                      >
                        Leave Review
                      </Button>
                    )}
                  </div>

                  {activeRentalId === rental.id && (
                    <div className="border-t pt-4 space-y-3">
                      {reviewError && (
                        <div className="p-3 rounded bg-red-100 text-red-700 text-sm">{reviewError}</div>
                      )}

                      <div>
                        <label className="text-sm text-gray-500">Rating</label>
                        <select
                          value={rating}
                          onChange={(e) => setRating(Number(e.target.value))}
                          className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
                        >
                          {[1, 2, 3, 4, 5].map((r) => (
                            <option key={r} value={r}>
                              {r} Star{r > 1 ? "s" : ""}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="text-sm text-gray-500">Comment (optional)</label>
                        <textarea
                          value={comment}
                          onChange={(e) => setComment(e.target.value)}
                          className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
                          rows={3}
                          placeholder="Share your thoughts..."
                        />
                      </div>

                      <div className="flex gap-2">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => handleSubmitReview(rental.id)}
                          isLoading={isSubmitting}
                        >
                          Submit Review
                        </Button>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => {
                            setActiveRentalId(null);
                            setRating(5);
                            setComment("");
                            setReviewError("");
                          }}
                        >
                          Cancel
                        </Button>
                      </div>
                    </div>
                  )}
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-6 text-gray-500">No pending reviews</div>
        )}
      </div>

      <div>
        <h2 className="text-2xl font-semibold mb-4">My Submitted Reviews</h2>
        {reviews.length > 0 ? (
          <div className="space-y-4">
            {reviews.map((review) => (
              <Card key={review.id}>
                <div className="space-y-2">
                  {editingReviewId === review.id ? (
                    <div className="space-y-3">
                      {reviewError && (
                        <div className="p-3 rounded bg-red-100 text-red-700 text-sm">{reviewError}</div>
                      )}
                      <div>
                        <h3 className="font-semibold text-lg mb-2">{review.vhs.title}</h3>
                      </div>
                      <div>
                        <label className="text-sm text-gray-500">Rating</label>
                        <select
                          value={editRating}
                          onChange={(e) => setEditRating(Number(e.target.value))}
                          className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
                        >
                          {[1, 2, 3, 4, 5].map((r) => (
                            <option key={r} value={r}>
                              {r} Star{r > 1 ? "s" : ""}
                            </option>
                          ))}
                        </select>
                      </div>
                      <div>
                        <label className="text-sm text-gray-500">Comment (optional)</label>
                        <textarea
                          value={editComment}
                          onChange={(e) => setEditComment(e.target.value)}
                          className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2"
                          rows={3}
                        />
                      </div>
                      <div className="flex gap-2">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => handleUpdateReview(review.id)}
                          isLoading={isSubmitting}
                        >
                          Save
                        </Button>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => {
                            setEditingReviewId(null);
                            setEditRating(5);
                            setEditComment("");
                            setReviewError("");
                          }}
                        >
                          Cancel
                        </Button>
                      </div>
                    </div>
                  ) : (
                    <>
                      <div className="flex justify-between items-start">
                        <div>
                          <h3 className="font-semibold text-lg">{review.vhs.title}</h3>
                          <p className="text-sm text-gray-600">{review.vhs.genre}</p>
                        </div>
                        <span className="text-yellow-600 font-semibold">{review.rating} ★</span>
                      </div>
                      {review.comment && <p className="text-gray-700">{review.comment}</p>}
                      <div className="flex gap-2 mt-3">
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => startEditReview(review)}
                        >
                          Edit
                        </Button>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => handleDeleteReview(review.id)}
                        >
                          Delete
                        </Button>
                      </div>
                    </>
                  )}
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="text-center py-6 text-gray-500">No reviews submitted yet</div>
        )}
      </div>
    </div>
  );
};
