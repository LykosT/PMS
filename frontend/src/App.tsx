import { Fragment } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardPage from './pages/DashboardPage'
import EmployeeFormPage from './pages/EmployeeFormPage'
import EmployeesPage from './pages/EmployeesPage'
import LoginPage from './pages/LoginPage'
import ResourceFormPage from './pages/ResourceFormPage'
import ResourceListPage from './pages/ResourceListPage'
import { RESOURCES } from './resources/config'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/employees" element={<EmployeesPage />} />
              <Route element={<ProtectedRoute roles={['ADMIN', 'MANAGER']} />}>
                <Route path="/employees/new" element={<EmployeeFormPage />} />
                <Route path="/employees/:id/edit" element={<EmployeeFormPage />} />
              </Route>
              {RESOURCES.map((config) => (
                <Fragment key={config.route}>
                  <Route
                    path={`/${config.route}`}
                    element={<ResourceListPage key={config.route} config={config} />}
                  />
                  <Route element={<ProtectedRoute roles={['ADMIN', 'MANAGER']} />}>
                    <Route
                      path={`/${config.route}/new`}
                      element={<ResourceFormPage key={config.route} config={config} />}
                    />
                    <Route
                      path={`/${config.route}/:id/edit`}
                      element={<ResourceFormPage key={`${config.route}-edit`} config={config} />}
                    />
                  </Route>
                </Fragment>
              ))}
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
