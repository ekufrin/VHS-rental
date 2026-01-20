import { Layout } from "../components/layout/Layout";
import { ProtectedRoute } from "../components/layout/ProtectedRoute";
import { ProfileForm } from "../features/profile/ProfileForm";

export default function ProfilePage() {
  return (
    <ProtectedRoute>
      <Layout>
        <ProfileForm />
      </Layout>
    </ProtectedRoute>
  );
}
