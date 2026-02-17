# AGENTS.md - Frontend Project Guide

## Project Overview

This is a React 19 + TypeScript frontend for the task-tube project. Built with Create React App, Material-UI components, and React Router for client-side routing. TanStack React Query is available for server state management and API data fetching.

**Key Stack:**

- React 19.2.4
- TypeScript 4.9.5
- React Router 7.13.0
- Material-UI (@mui) 7.3.8
- TanStack React Query 5.90.21
- Jest + React Testing Library for tests

## Dev Environment Tips

- Start the dev server with `npm start` - runs on http://localhost:3000 with hot reload.
- TypeScript strict mode is enabled - fix all type errors before committing.
- Material-UI components are imported from `@mui/material` - use them for consistent UI.
- Check `src/components/` for existing components before creating new ones.
- The app uses React Router - update routing in `src/components/app/App.tsx` if adding new routes.
- React Query is configured and ready for backend API calls - set up queries in components as needed.

## Testing Instructions

- Run `npm test` to start Jest in watch mode or `npm test -- --watchAll=false` for a single run.
- All test files should follow the pattern `*.test.tsx` or `*.test.ts` and be placed next to the component they test.
- Use React Testing Library utilities: `render()`, `screen`, `fireEvent`, `waitFor` for testing components.
- Example test command: `npm test -- --testNamePattern="<test name>"` to run a specific test.
- Run `npm lint` to check for ESLint and TypeScript errors (ESLint strict rules are enabled).
- Run `npm lint --fix` to automatically fix linting issues.
- The commit must pass both `npm lint` and `npm test` before merging.
- Add or update tests for any code changes, even if not explicitly requested.

## Code Style & Linting

- **Prettier** is configured with 100 char line width, 2-space indentation, single quotes.
- **ESLint** enforces React/TypeScript best practices including React Hooks rules.
- Run `npm lint --fix` to auto-format code before committing.
- TypeScript: All code must pass strict type checking - no `any` types without justification.

## PR Instructions

- Title format: `[frontend] <Description>`
- Always run `npm lint` and `npm test` before committing.
- Ensure no TypeScript errors exist in the entire `src/` directory.
- Include tests for new components or features.
- Use Material-UI components for UI consistency.
- Keep components in `src/components/` organized by feature (e.g., `src/components/tasks/`).

## Project Structure

```
src/
├── components/
│   ├── app/          (Main App and routing)
│   └── tasks/        (Task management feature)
├── index.tsx         (React entry point)
├── setupTests.ts     (Jest configuration)
└── react-app-env.d.ts (TypeScript definitions)
```

## Common Tasks

- **Add a new component:** Create file in `src/components/<feature>/`, export as default, add corresponding `.test.tsx` file.
- **Connect to backend API:** Use TanStack React Query hooks like `useQuery()` or `useMutation()`.
- **Style components:** Use Material-UI `sx` prop or `styled()` from `@emotion/styled`, already configured.
- **Add a new route:** Update `src/components/app/App.tsx` with a new Route in the Routes component.
