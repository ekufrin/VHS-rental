import React, { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useNavigate } from "react-router";
import { Input } from "../../components/ui/Input";
import { Button } from "../../components/ui/Button";
import { useAuthStore } from "../../stores/authStore";
import { authApi } from "../../api/authApi";
import { userApi } from "../../api/userApi";
import { handleApiError } from "../../api/errorHandler";

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().nonempty("Password is required"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const LoginForm: React.FC = () => {
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { setAccessToken, setUser } = useAuthStore();

  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    setError("");
    setIsLoading(true);

    try {
      const response = await authApi.login(data);
      setAccessToken(response.accessToken);
      const user = await userApi.getMe();
      setUser(user);
      navigate("/");
    } catch (err) {
      const apiError = handleApiError(err);
      const errorMessage = apiError.detail || apiError.title || `Error ${apiError.status || ""}`.trim() || "Authentication failed";
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      {error && <div className="p-4 bg-red-100 text-red-700 rounded-lg">{error}</div>}

      <Input
        label="Email"
        type="email"
        placeholder="you@example.com"
        {...register("email")}
        error={errors.email?.message}
      />

      <Input
        label="Password"
        type="password"
        placeholder="••••••"
        {...register("password")}
        error={errors.password?.message}
      />

      <Button type="submit" isLoading={isLoading} className="w-full">
        Sign In
      </Button>
    </form>
  );
};
