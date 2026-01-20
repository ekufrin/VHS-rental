import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("login", "routes/auth/login.tsx"),
  route("register", "routes/auth/register.tsx"),
  route("vhs", "routes/vhs/index.tsx"),
  route("vhs/add", "routes/vhs/add.tsx"),
  route("vhs/:id", "routes/vhs/$id.tsx"),
  route("rentals", "routes/rentals/index.tsx"),
  route("reviews", "routes/reviews/index.tsx"),
  route("genres", "routes/genres/index.tsx"),
  route("profile", "routes/profile.tsx"),
] satisfies RouteConfig;
