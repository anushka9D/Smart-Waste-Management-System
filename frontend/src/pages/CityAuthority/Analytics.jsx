import React, { useState, useEffect } from 'react';
import { smartBinService } from '../../services/smartBinService';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';
import { Bar, Pie } from 'react-chartjs-2';


ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

function Analytics() {
  const [wasteByLocationData, setWasteByLocationData] = useState(null);
  const [wasteByTypeData, setWasteByTypeData] = useState(null);
  const [binStatusData, setBinStatusData] = useState(null);
  const [summaryData, setSummaryData] = useState({
    totalWaste: 0,
    plasticWaste: 0,
    organicWaste: 0,
    metalWaste: 0,
    totalBins: 0
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedWasteType, setSelectedWasteType] = useState('all');

  useEffect(() => {
    fetchData();
  }, [selectedWasteType]);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Fetch summary data
      const [
        totalWasteResponse,
        plasticResponse,
        organicResponse,
        metalResponse,
        binStatusResponse
      ] = await Promise.all([
        smartBinService.getTotalWaste(),
        smartBinService.getTotalPlasticWaste(),
        smartBinService.getTotalOrganicWaste(),
        smartBinService.getTotalMetalWaste(),
        smartBinService.getBinStatus()
      ]);
      
      // Calculate total bins
      const totalBins = binStatusResponse.data?.reduce((sum, status) => sum + (status.count || 0), 0) || 0;
      
      setSummaryData({
        totalWaste: totalWasteResponse.data?.totalWaste || 0,
        plasticWaste: plasticResponse.data || 0,
        organicWaste: organicResponse.data || 0,
        metalWaste: metalResponse.data || 0,
        totalBins: totalBins
      });
      
      // Fetch chart data based on selected waste type
      if (selectedWasteType === 'all') {
        const [
          locationChartResponse,
          wasteTypeChartResponse,
          statusChartResponse
        ] = await Promise.all([
          smartBinService.getLocationChartData(),
          smartBinService.getWasteTypeChartData(),
          smartBinService.getStatusChartData()
        ]);
        
        processLocationChartData(locationChartResponse.data);
        processWasteTypeChartData(wasteTypeChartResponse.data);
        processStatusChartData(statusChartResponse.data);
      } else {
        const [
          locationResponse,
          statusResponse
        ] = await Promise.all([
          smartBinService.getWasteByLocationFiltered(selectedWasteType),
          smartBinService.getBinStatus()
        ]);
        
        
        const locationChartData = locationResponse.data?.map(item => ({
          label: item.location,
          value: item.totalWaste
        })) || [];
        
       
        const statusChartData = statusResponse.data?.map(item => ({
          label: item.status,
          value: item.count
        })) || [];
        
        processLocationChartData(locationChartData);
        processStatusChartData(statusChartData);
        
        
        const wasteValue = selectedWasteType === 'plastic' ? (plasticResponse.data || 0) : 
                          selectedWasteType === 'organic' ? (organicResponse.data || 0) : 
                          (metalResponse.data || 0);
        
        const wasteTypeData = [{
          label: selectedWasteType.charAt(0).toUpperCase() + selectedWasteType.slice(1),
          value: wasteValue
        }];
        
        setWasteByTypeData({
          labels: wasteTypeData.map(item => item.label),
          datasets: [
            {
              label: 'Waste by Type (kg)',
              data: wasteTypeData.map(item => item.value),
              backgroundColor: 'rgba(54, 162, 235, 0.6)',
              borderColor: 'rgba(54, 162, 235, 1)',
              borderWidth: 1,
            },
          ],
        });
      }
    } catch (err) {
      console.error('Error fetching analytics data:', err);
      setError('Failed to fetch analytics data: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const processLocationChartData = (data) => {
    if (!data || !Array.isArray(data)) {
      setWasteByLocationData(null);
      return;
    }
    
    const labels = data.map(item => item.label || 'Unknown');
    const values = data.map(item => item.value || 0);
    
    setWasteByLocationData({
      labels,
      datasets: [
        {
          label: 'Waste by Location (kg)',
          data: values,
          backgroundColor: 'rgba(54, 162, 235, 0.6)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 1,
        },
      ],
    });
  };

  const processWasteTypeChartData = (data) => {
    if (!data || !Array.isArray(data)) {
      setWasteByTypeData(null);
      return;
    }
    
    const labels = data.map(item => item.label || 'Unknown');
    const values = data.map(item => item.value || 0);
    
    setWasteByTypeData({
      labels,
      datasets: [
        {
          label: 'Waste by Type (kg)',
          data: values,
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
          ],
          borderWidth: 1,
        },
      ],
    });
  };

  const processStatusChartData = (data) => {
    if (!data || !Array.isArray(data)) {
      setBinStatusData(null);
      return;
    }
    
    const labels = data.map(item => item.label || 'Unknown');
    const values = data.map(item => item.value || 0);
    
    setBinStatusData({
      labels,
      datasets: [
        {
          label: 'Bin Status Distribution',
          data: values,
          backgroundColor: [
            'rgba(255, 99, 132, 0.6)',
            'rgba(54, 162, 235, 0.6)',
            'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)',
            'rgba(153, 102, 255, 0.6)',
          ],
          borderColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
          ],
          borderWidth: 1,
        },
      ],
    });
  };

  const handleRefresh = () => {
    fetchData();
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
        <button 
          onClick={handleRefresh}
          className="mt-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
        >
          Retry
        </button>
      </div>
    );
  }

  // Chart options
  const barChartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Waste Analytics',
      },
    },
  };

  const pieChartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Distribution Charts',
      },
    },
  };

  return (
    <div className="p-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <h1 className="text-3xl font-bold text-gray-800">Waste Analytics</h1>
        <div className="flex flex-wrap gap-3">
          <button 
            onClick={handleRefresh}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Refresh Data
          </button>
        </div>
      </div>
      
     
      <div className="bg-white rounded-lg shadow p-6 mb-8">
        <h2 className="text-xl font-bold text-gray-800 mb-4">Filter Options</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Waste Type Filter</label>
            <select
              value={selectedWasteType}
              onChange={(e) => setSelectedWasteType(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="all">All Waste Types</option>
              <option value="plastic">Plastic</option>
              <option value="organic">Organic</option>
              <option value="metal">Metal</option>
            </select>
          </div>
        </div>
      </div>
      
     
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Total Waste</h3>
          <p className="mt-2 text-3xl font-bold text-blue-600">
            {summaryData.totalWaste} kg
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Plastic Waste</h3>
          <p className="mt-2 text-3xl font-bold text-green-600">
            {summaryData.plasticWaste} kg
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Organic Waste</h3>
          <p className="mt-2 text-3xl font-bold text-yellow-600">
            {summaryData.organicWaste} kg
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Metal Waste</h3>
          <p className="mt-2 text-3xl font-bold text-purple-600">
            {summaryData.metalWaste} kg
          </p>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-medium text-gray-900">Total Bins</h3>
          <p className="mt-2 text-3xl font-bold text-indigo-600">
            {summaryData.totalBins}
          </p>
        </div>
      </div>
      
     
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Waste by Location</h2>
          {wasteByLocationData ? (
            <Bar data={wasteByLocationData} options={barChartOptions} />
          ) : (
            <p className="text-gray-500">No location data available</p>
          )}
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Waste by Type</h2>
          {wasteByTypeData ? (
            <Pie data={wasteByTypeData} options={pieChartOptions} />
          ) : (
            <p className="text-gray-500">No waste type data available</p>
          )}
        </div>
      </div>
      
    
    </div>
  );
}

export default Analytics;