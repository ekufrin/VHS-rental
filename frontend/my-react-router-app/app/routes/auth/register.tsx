import { Card } from "../../components/ui/Card";
import { Layout } from "../../components/layout/Layout";
import { RegisterForm } from "../../features/auth/RegisterForm";
import { Link } from "react-router";

export default function RegisterPage() {
  return (
    <Layout>
      <div className="max-w-md mx-auto">
        <Card title="Create Account">
          <RegisterForm />
          <div className="mt-6 text-center">
            <p className="text-gray-600">
              Already have an account?{" "}
              <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">
                Sign in
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </Layout>
  );
}
