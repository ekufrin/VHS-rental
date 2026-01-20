import { Layout } from "../../components/layout/Layout";
import { ProtectedRoute } from "../../components/layout/ProtectedRoute";
import { MyReviews } from "../../features/review/MyReviews";

export default function MyReviewsPage() {
  return (
    <ProtectedRoute>
      <Layout>
        <MyReviews />
      </Layout>
    </ProtectedRoute>
  );
}
