import { Layout } from "../components/layout/Layout";
import { Link } from "react-router";
import { Card } from "../components/ui/Card";
import { Button } from "../components/ui/Button";
import { useAuthStore } from "../stores/authStore";

export default function HomePage() {
  const { isAuthenticated } = useAuthStore();

  return (
    <Layout>
      <div className="space-y-12">
        {/* Hero Section */}
        <div className="text-center py-12">
          <h1 className="text-5xl font-bold text-gray-900 mb-4">
            Welcome to VHS Rental
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            Your ultimate destination for classic video rentals
          </p>
          <Link to="/vhs">
            <Button variant="primary" size="lg">
              Browse Catalog
            </Button>
          </Link>
        </div>

        {/* Features */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card title="🎬 Vast Collection">
            <p className="text-gray-600">
              Access thousands of VHS titles from classic to contemporary films.
            </p>
          </Card>
          <Card title="💰 Affordable Rentals">
            <p className="text-gray-600">
              Competitive pricing with flexible rental periods tailored to your needs.
            </p>
          </Card>
          <Card title="📦 Easy Returns">
            <p className="text-gray-600">
              Hassle-free returns with multiple convenient drop-off locations.
            </p>
          </Card>
        </div>

        {/* Call to Action - Only show for non-authenticated users */}
        {!isAuthenticated && (
          <div className="bg-blue-600 text-white rounded-lg p-12 text-center">
            <h2 className="text-3xl font-bold mb-4">Ready to get started?</h2>
            <p className="text-lg mb-6">
              Create your account today and start enjoying your favorite films.
            </p>
            <div className="flex gap-4 justify-center">
              <Link to="/register">
                <Button variant="secondary" size="lg">
                  Create Account
                </Button>
              </Link>
              <Link to="/vhs">
                <Button variant="primary" size="lg">
                  Browse VHS
                </Button>
              </Link>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
}
