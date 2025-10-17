import React, { useState, useEffect } from 'react';
import { smartBinService } from '../../services/smartBinService';
import jsPDF from 'jspdf';
import 'jspdf-autotable';

function Reports() {
    const [reportData, setReportData] = useState({
        wasteByLocation: [],
        wasteByType: [],
        binStatus: [],
        plasticTotal: 0,
        organicTotal: 0,
        metalTotal: 0,
        locationWithMostWaste: null
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedWasteType, setSelectedWasteType] = useState('all');
    const [reportType, setReportType] = useState('detailed');

    useEffect(() => {
        fetchData();
    }, [selectedWasteType]);

    

    const fetchData = async () => {
        try {
            console.log('Starting to fetch report data...');
            setLoading(true);
            setError(null);



            console.log('Service methods available:', {
                getWasteByLocation: typeof smartBinService.getWasteByLocation,
                getWasteByType: typeof smartBinService.getWasteByType,
                getBinStatus: typeof smartBinService.getBinStatus,
                getTotalPlasticWaste: typeof smartBinService.getTotalPlasticWaste,
                getTotalOrganicWaste: typeof smartBinService.getTotalOrganicWaste,
                getTotalMetalWaste: typeof smartBinService.getTotalMetalWaste,
                getLocationWithMostWaste: typeof smartBinService.getLocationWithMostWaste
            });


            if (selectedWasteType === 'all') {

                console.log('Making API calls for all waste types...');
                const [
                    wasteByLocationResponse,
                    wasteByTypeResponse,
                    binStatusResponse,
                    plasticResponse,
                    organicResponse,
                    metalResponse,
                    locationResponse
                ] = await Promise.all([
                    smartBinService.getWasteByLocation(),
                    smartBinService.getWasteByType(),
                    smartBinService.getBinStatus(),
                    smartBinService.getTotalPlasticWaste(),
                    smartBinService.getTotalOrganicWaste(),
                    smartBinService.getTotalMetalWaste(),
                    smartBinService.getLocationWithMostWaste()
                ]);

                console.log('API responses received:', {
                    wasteByLocation: wasteByLocationResponse,
                    wasteByType: wasteByTypeResponse,
                    binStatus: binStatusResponse,
                    plastic: plasticResponse,
                    organic: organicResponse,
                    metal: metalResponse,
                    location: locationResponse
                });

                setReportData({
                    wasteByLocation: wasteByLocationResponse.data || [],
                    wasteByType: wasteByTypeResponse.data || [],
                    binStatus: binStatusResponse.data || [],
                    plasticTotal: plasticResponse.data || 0,
                    organicTotal: organicResponse.data || 0,
                    metalTotal: metalResponse.data || 0,
                    locationWithMostWaste: locationResponse.data || null
                });
            } else {

                console.log('Making API calls for waste type:', selectedWasteType);
                try {
                    const [
                        wasteByLocationResponse,
                        wasteByTypeResponse,
                        binStatusResponse
                    ] = await Promise.all([
                        smartBinService.getWasteByLocationFiltered(selectedWasteType),
                        smartBinService.getWasteByTypeFiltered(selectedWasteType),
                        smartBinService.getBinStatus()
                    ]);


                    let plasticTotal = 0, organicTotal = 0, metalTotal = 0;
                    let locationResponse = { data: null };

                    try {
                        locationResponse = await smartBinService.getLocationWithMostWaste();
                    } catch (locationError) {
                        console.error('Error fetching location data:', locationError);
                        locationResponse = { data: null };
                    }

                    if (selectedWasteType === 'plastic') {
                        try {
                            const plasticResponse = await smartBinService.getTotalPlasticWaste();
                            plasticTotal = plasticResponse.data || 0;
                        } catch (error) {
                            console.error('Error fetching plastic waste data:', error);
                        }
                    } else if (selectedWasteType === 'organic') {
                        try {
                            const organicResponse = await smartBinService.getTotalOrganicWaste();
                            organicTotal = organicResponse.data || 0;
                        } catch (error) {
                            console.error('Error fetching organic waste data:', error);
                        }
                    } else if (selectedWasteType === 'metal') {
                        try {
                            const metalResponse = await smartBinService.getTotalMetalWaste();
                            metalTotal = metalResponse.data || 0;
                        } catch (error) {
                            console.error('Error fetching metal waste data:', error);
                        }
                    }

                    console.log('Filtered API responses received:', {
                        wasteByLocation: wasteByLocationResponse,
                        wasteByType: wasteByTypeResponse,
                        binStatus: binStatusResponse,
                        location: locationResponse,
                        plasticTotal,
                        organicTotal,
                        metalTotal
                    });

                    setReportData({
                        wasteByLocation: wasteByLocationResponse.data || [],
                        wasteByType: wasteByTypeResponse.data || [],
                        binStatus: binStatusResponse.data || [],
                        plasticTotal: plasticTotal,
                        organicTotal: organicTotal,
                        metalTotal: metalTotal,
                        locationWithMostWaste: locationResponse.data || null
                    });
                } catch (filterError) {
                    console.error('Error fetching filtered data:', filterError);
                    console.error('Error details:', {
                        message: filterError.message,
                        stack: filterError.stack,
                        response: filterError.response
                    });
                    setError(`Failed to fetch filtered report data: ${filterError.message || filterError}`);

                    setReportData({
                        wasteByLocation: [],
                        wasteByType: [],
                        binStatus: [],
                        plasticTotal: 0,
                        organicTotal: 0,
                        metalTotal: 0,
                        locationWithMostWaste: null
                    });
                }
            }

            console.log('Report data set successfully:', reportData);
        } catch (err) {
            console.error('Error fetching report data:', err);
            console.error('Error details:', {
                message: err.message,
                stack: err.stack,
                response: err.response
            });
            setError(`Failed to fetch report data: ${err.message || err}`);
        } finally {
            setLoading(false);
        }
    };


    const handleRefresh = () => {
        console.log('Manual refresh triggered');
        fetchData();
    };




    const filterDataByWasteType = (data) => {
        if (!data || !Array.isArray(data)) return [];
        if (selectedWasteType === 'all') return data;
        return data.filter(item =>
            item.wasteType && item.wasteType.toLowerCase() === selectedWasteType.toLowerCase()
        );
    };


    const generatePDF = () => {
        try {
            const doc = new jsPDF();


            doc.setFontSize(20);
            doc.text('Waste Management Report', 105, 20, null, null, 'center');


            doc.setFontSize(12);
            doc.text(`Report Type: ${reportType === 'detailed' ? 'Detailed Report' : 'Summary Report'}`, 20, 30);
            doc.text(`Generated on: ${new Date().toLocaleDateString()}`, 20, 37);
            doc.text(`Waste Type: ${selectedWasteType === 'all' ? 'All Types' : selectedWasteType}`, 20, 44);

            if (reportType === 'summary') {

                generateSummaryReport(doc);
            } else {

                generateDetailedReport(doc);
            }


            doc.save(`waste-management-report-${reportType}-${new Date().toISOString().slice(0, 10)}.pdf`);
        } catch (error) {
            console.error('Error generating PDF:', error);
            alert('Error generating PDF report. Please try again.');
        }
    };


    const generateSummaryReport = (doc) => {

        doc.setFontSize(16);
        doc.text('Waste by Type Summary', 20, 60);

        doc.setFontSize(12);
        doc.text('Plastic Waste: ' + (reportData.plasticTotal || 0) + ' kg', 20, 70);
        doc.text('Organic Waste: ' + (reportData.organicTotal || 0) + ' kg', 20, 77);
        doc.text('Metal Waste: ' + (reportData.metalTotal || 0) + ' kg', 20, 84);
        doc.text('Total Waste: ' + ((reportData.plasticTotal || 0) + (reportData.organicTotal || 0) + (reportData.metalTotal || 0)) + ' kg', 20, 91);


        if (reportData.locationWithMostWaste && reportData.locationWithMostWaste.location !== "No data") {
            doc.text('Highest Waste Location: ' + (reportData.locationWithMostWaste.location || 'Unknown') +
                ' (' + (reportData.locationWithMostWaste.totalWaste || 0) + ' kg)', 20, 105);
        }


        doc.setFontSize(16);
        doc.text('Bin Status Summary', 20, 120);

        let yPos = 130;
        if (reportData.binStatus && Array.isArray(reportData.binStatus)) {
            reportData.binStatus.forEach(status => {
                doc.text((status.status || 'Unknown') + ': ' + (status.count || 0) + ' bins (' + (status.percentage || 0).toFixed(1) + '%)', 20, yPos);
                yPos += 7;
            });
        }
    };


    const generateDetailedReport = (doc) => {
        try {

            doc.setFontSize(16);
            doc.text('Waste by Type', 20, 60);

            const wasteByTypeData = filterDataByWasteType(reportData.wasteByType);
            let startY = 65;

            if (wasteByTypeData && wasteByTypeData.length > 0) {
                doc.autoTable({
                    startY: startY,
                    head: [['Waste Type', 'Total (kg)', 'Bins', 'Percentage']],
                    body: wasteByTypeData.map(item => [
                        item.wasteType || 'Unknown',
                        item.totalWaste || 0,
                        item.binCount || 0,
                        (item.percentage || 0).toFixed(1) + '%'
                    ]),
                    theme: 'grid'
                });
                startY = doc.lastAutoTable.finalY + 15;
            } else {
                doc.setFontSize(12);
                doc.text('No data available for selected waste type', 20, startY + 5);
                startY += 15;
            }


            doc.setFontSize(16);
            doc.text('Location Analysis', 20, startY);

            if (reportData.wasteByLocation && reportData.wasteByLocation.length > 0) {
                doc.autoTable({
                    startY: startY + 10,
                    head: [['Location', 'Total Waste (kg)', 'Number of Bins', 'Average per Bin (kg)']],
                    body: reportData.wasteByLocation.map(item => [
                        item.location || 'Unknown',
                        item.totalWaste || 0,
                        item.binCount || 0,
                        (item.averageWaste || 0).toFixed(1)
                    ]),
                    theme: 'grid'
                });
                startY = doc.lastAutoTable.finalY + 15;
            } else {
                doc.setFontSize(12);
                doc.text('No location data available', 20, startY + 15);
                startY += 25;
            }


            doc.setFontSize(16);
            doc.text('Bin Status Report', 20, startY);

            if (reportData.binStatus && reportData.binStatus.length > 0) {
                doc.autoTable({
                    startY: startY + 10,
                    head: [['Status', 'Number of Bins', 'Percentage']],
                    body: reportData.binStatus.map(item => [
                        item.status || 'Unknown',
                        item.count || 0,
                        (item.percentage || 0).toFixed(1) + '%'
                    ]),
                    theme: 'grid'
                });
            } else {
                doc.setFontSize(12);
                doc.text('No bin status data available', 20, startY + 15);
            }
        } catch (error) {
            console.error('Error generating detailed report:', error);
            doc.setFontSize(12);
            doc.text('Error generating report: ' + error.message, 20, 60);
        }
    };

    if (loading) {
        return (
            <div className="flex flex-col justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
                <span className="ml-4 mt-4">Loading report data...</span>
            </div>
        );
    }

    if (error) {
        return (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative" role="alert">
                <strong className="font-bold">Error! </strong>
                <span className="block sm:inline">{error}</span>
                <div className="mt-2">
                    <button
                        onClick={handleRefresh}
                        className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 mr-2"
                    >
                        Retry
                    </button>
                    <button
                        onClick={fetchData}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Refresh
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
                <h1 className="text-3xl font-bold text-gray-800">Waste Management Reports</h1>
                <div className="flex flex-wrap gap-3">
                    <button
                        onClick={handleRefresh}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Refresh Data
                    </button>

                    <button
                        onClick={generatePDF}
                        className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 flex items-center"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M3 17a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm3.293-7.707a1 1 0 011.414 0L9 10.586V3a1 1 0 112 0v7.586l1.293-1.293a1 1 0 111.414 1.414l-3 3a1 1 0 01-1.414 0l-3-3a1 1 0 010-1.414z" clipRule="evenodd" />
                        </svg>
                        Generate PDF
                    </button>
                </div>
            </div>


            <div className="bg-white rounded-lg shadow p-6 mb-8">
                <h2 className="text-xl font-bold text-gray-800 mb-4">Report Options</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Report Type</label>
                        <div className="flex space-x-4">
                           
                            <label className="inline-flex items-center">
                                <input
                                    type="radio"
                                    className="form-radio"
                                    name="reportType"
                                    value="summary"
                                    checked={reportType === 'summary'}
                                    onChange={(e) => setReportType(e.target.value)}
                                />
                                <span className="ml-2">Summary Report</span>
                            </label>
                        </div>
                    </div>

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


        </div>
    );
}

export default Reports;