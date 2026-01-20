# VHS Rental Frontend

A modern React-based frontend application for the VHS Rental System, built with React Router v7, TypeScript, TailwindCSS, and Zustand for state management.

## Architecture Overview

### Tech Stack
- **React 19** - UI library
- **React Router v7** - Full-stack routing with SSR
- **TypeScript** - Type safety
- **TailwindCSS** - Utility-first CSS
- **Zustand** - Client state management (auth tokens)
- **React Hook Form + Zod** - Form handling and validation
- **Axios** - HTTP client with interceptors
- **date-fns** - Date formatting utilities

### Project Structure

```
app/
├── api/                    # API layer with domain-specific modules
│   ├── client.ts          # Axios instance with interceptors
│   ├── errorHandler.ts    # Centralized error handling
│   ├── authApi.ts         # Auth endpoints
│   ├── vhsApi.ts          # VHS catalog endpoints
│   ├── rentalApi.ts       # Rental management endpoints
│   ├── reviewApi.ts       # Review endpoints
│   ├── genreApi.ts        # Genre endpoints
│   └── userApi.ts         # User profile endpoints
├── stores/                 # Zustand state management
│   └── authStore.ts       # Auth token & user state (persistent)
├── components/
│   ├── ui/                # Reusable UI components
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Card.tsx
│   │   ├── Modal.tsx
│   │   ├── Pagination.tsx
│   │   └── SearchBar.tsx
│   └── layout/            # Layout components
│       ├── Header.tsx     # Navigation header
│       ├── Layout.tsx     # Main layout wrapper
│       └── ProtectedRoute.tsx
├── features/              # Feature-specific components
│   ├── auth/              # Login/Register forms
│   ├── vhs/               # VHS catalog pages
│   └── rental/            # Rental management
├── routes/                # Page routes (React Router)
│   ├── home.tsx           # Landing page
│   ├── auth/
│   │   ├── login.tsx
│   │   └── register.tsx
│   ├── vhs/
│   │   ├── index.tsx      # VHS list page
│   │   └── $id.tsx        # VHS detail page
│   └── rentals/
│       └── index.tsx      # Rental list page
├── types/                 # TypeScript interfaces
│   └── index.ts           # All domain types
└── root.tsx               # Root layout & error boundary
```

## Key Features

### Authentication & Security
- JWT-based authentication with access & refresh tokens
- Refresh tokens stored in httpOnly cookies (secure)
- Access tokens in memory (logout on page reload)
- Automatic token refresh on 401 responses
- Protected routes with redirect to login

### API Integration
- Centralized API client with Axios interceptors
- Domain-specific API modules (auth, vhs, rental, review, genre, user)
- Standardized error handling for both ApiResponse and ProblemDetail formats
- Support for multipart/form-data (VHS image uploads)

### State Management
- Zustand for minimal, reactive auth state
- LocalStorage persistence for tokens
- No Redux/Context overhead - just focused auth state

### Form Handling
- React Hook Form with Zod validation
- Type-safe form data matching backend constraints
- Real-time validation feedback

### UI/UX
- Responsive Tailwind CSS styling
- Compound component patterns for reusability
- Pagination with smart page navigation
- Search and filter capabilities
- Loading states and error handling

## Getting Started

### Prerequisites
- Node.js 18+
- Backend API running on `http://localhost:8080/api`

### Installation

1. **Clone and navigate to project:**
```bash
cd my-react-router-app
```

2. **Install dependencies:**
```bash
npm install
```

3. **Configure environment:**
```bash
cp .env.example .env
# Edit .env if your backend runs on a different URL
```

### Development

Start the development server with HMR:

```bash
npm run dev
```

Your app will be available at `http://localhost:5173`.

### Building for Production

```bash
npm run build
```

Output structure:
```
build/
├── client/    # Static assets
└── server/    # Server-side code
```

### Running Production Build

```bash
npm start
```

## API Endpoints Integration

All API interactions go through `app/api/*Api.ts` modules:

### Auth Flow
1. User logs in via `authApi.login()`
2. Access token stored in memory, refresh token in cookie
3. Axios interceptor adds `Authorization: Bearer <token>` header
4. On 401: automatic retry with refreshed token via `/auth/access-token`
5. On persistent failure: user logged out and redirected to login

### Data Fetching
- Pagination uses Spring Data format: `{ page, size, sort }`
- Responses follow `ApiResponse<T>` or `Page<T>` structure
- Error responses parsed from `ProblemDetail` format

## Validation Schemas

Form validation uses Zod schemas colocated with forms:
- `LoginForm`: email + password validation
- `RegisterForm`: name + email + password confirmation
- Backend constraint matching: email format, password length, etc.

## Component Patterns

### Protected Routes
Wrap components with `<ProtectedRoute>`:
```tsx
<ProtectedRoute>
  <YourComponent />
</ProtectedRoute>
```

### Loading States
Components manage their own loading:
```tsx
const [isLoading, setIsLoading] = useState(false);
// in handlers: try { setIsLoading(true); ... } finally { setIsLoading(false); }
```

### Error Handling
Standardized via `handleApiError()`:
```tsx
try {
  const data = await someApi.call();
} catch (err) {
  const apiError = handleApiError(err);
  console.log(apiError.detail, apiError.status);
}
```

## Environment Configuration

Create `.env` file with:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

For different deployments:
- Development: `http://localhost:8080/api`
- Production: Update to your production API URL

## Styling

Uses Tailwind CSS v4 with `@tailwindcss/vite` plugin for instant class compilation. No CSS modules needed - leverage utility classes.

## Type Safety

Full TypeScript coverage:
- All API responses typed
- Form data validation through Zod inference
- React Router v7 supports type-safe route params via `useParams<Route.Params>()`

## Next Steps

After initial setup, you can:
1. **Add more features**: Create new modules in `features/` and routes in `routes/`
2. **Customize styles**: Modify Tailwind config or create component-level styles
3. **Extend auth**: Add role-based access control (RBAC) checks in `ProtectedRoute`
4. **Add caching**: Implement request deduplication or React Query-like patterns
5. **Deploy**: Build and deploy to your hosting platform (Vercel, AWS, etc.)

## Troubleshooting

### "API not responding"
- Ensure backend is running on correct URL in `.env`
- Check CORS settings on backend
- Verify `VITE_API_BASE_URL` matches your API

### "Token refresh loop"
- Refresh token expired or invalid
- Clear localStorage and login again
- Check backend refresh token expiration settings

### "Routes not working"
- Verify route definitions in `app/routes.ts`
- React Router v7 uses file-based routing alongside explicit config
- Check file naming: use `$id.tsx` for route params

---

Built with ❤️ using React Router v7 and TailwindCSS.

