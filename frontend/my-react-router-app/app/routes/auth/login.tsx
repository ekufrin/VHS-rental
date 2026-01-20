import { Card } from "../../components/ui/Card";
import { Layout } from "../../components/layout/Layout";
import { LoginForm } from "../../features/auth/LoginForm";
import { Link } from "react-router";

export default function LoginPage() {
  return (
    <Layout>
      <div className="max-w-md mx-auto">
        <Card title="Sign In to VHS Rental">
          <LoginForm />
          <div className="mt-6 text-center">
            <p className="text-gray-600">
              Don't have an account?{" "}
              <Link to="/register" className="text-blue-600 hover:text-blue-700 font-medium">
                Create one
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </Layout>
  );
}
