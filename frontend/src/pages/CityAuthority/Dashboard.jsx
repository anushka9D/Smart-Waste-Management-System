import React, { useState, useEffect } from 'react';
import { smartBinService } from '../../services/smartBinService';

function Dashboard() {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const response = await smartBinService.getDashboardData();
      console.log("dashboard data", response.data);
      setDashboardData(response.data);
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
        <strong className="font-bold">Error! </strong>
        <span className="block sm:inline">{error}</span>
      </div>
    );
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Dashboard</h1>

      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Total Waste</h3>
          <p className="mt-2 text-3xl font-bold text-blue-600">
            {dashboardData?.totalWaste?.totalWaste || 0} kg
          </p>
          <p className="mt-1 text-sm text-gray-500">Overall waste collected</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Total Capacity</h3>
          <p className="mt-2 text-3xl font-bold text-green-600">
            {dashboardData?.totalWaste?.totalCapacity || 0} kg
          </p>
          <p className="mt-1 text-sm text-gray-500">Combined bin capacity</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Utilization Rate</h3>
          <p className="mt-2 text-3xl font-bold text-yellow-600">
            {dashboardData?.totalWaste?.utilizationRate?.toFixed(1) || 0}%
          </p>
          <p className="mt-1 text-sm text-gray-500">Overall bin utilization</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Total Bins</h3>
          <p className="mt-2 text-3xl font-bold text-purple-600">
            {dashboardData?.binStatus?.reduce((sum, status) => sum + status.count, 0) || 0}
          </p>
          <p className="mt-1 text-sm text-gray-500">Smart bins deployed</p>
        </div>
      </div>

     
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Top Locations by Waste</h2>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Location</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Waste (kg)</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bins</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {dashboardData?.topLocations?.map((location, index) => (
                  <tr key={index}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{location.location}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{location.totalWaste}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{location.binCount}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Bin Status Overview</h2>
          <div className="space-y-4">
            {dashboardData?.binStatus?.map((status, index) => (
              <div key={index}>
                <div className="flex justify-between mb-1">
                  <span className="font-medium text-gray-700">{status.status}</span>
                  <span className="text-gray-600">{status.count} bins ({status.percentage.toFixed(1)}%)</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    className="bg-green-600 h-2.5 rounded-full"
                    style={{ width: `${status.percentage}%` }}
                  ></div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;