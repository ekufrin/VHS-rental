import { Layout } from "../../components/layout/Layout";
import { RentalList } from "../../features/rental/RentalList";
import { ProtectedRoute } from "../../components/layout/ProtectedRoute";

export default function RentalsPage() {
  return (
    <ProtectedRoute>
      <Layout>
        <RentalList />
      </Layout>
    </ProtectedRoute>
  );
}
