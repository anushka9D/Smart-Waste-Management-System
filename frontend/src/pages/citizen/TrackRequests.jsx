import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';
import { getCitizenRequests } from '../../services/api';

function TrackRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const requestsPerPage = 15;
  const navigate = useNavigate();

  const statusDisplay = {
    'SUBMITTED': 'Submitted',
    'ASSIGNED': 'Assigned', 
    'COLLECTION_SCHEDULED': 'Collection Scheduled',
    'OUT_FOR_COLLECTION': 'Out for collection',
    'COLLECTED': 'Collected',
    'RESOLVED': 'Resolved',
    'CANCELLED': 'Cancelled'
  };

  const categoryDisplay = {
    'OVERFLOWING_BIN': 'Overflowing Bin',
    'DAMAGED_BIN': 'Damaged Bin',
    'MISSING_BIN': 'Missing Bin',
    'ILLEGAL_DUMPING': 'Illegal Dumping',
    'REGULAR_PICKUP_REQUEST': 'Regular Pickup Request',
    'OTHER': 'Other'
  };

  useEffect(() => {
    fetchRequests();
  }, [currentPage]);

  const fetchRequests = async () => {
    try {
      const response = await getCitizenRequests(currentPage, requestsPerPage);
      // Handle ApiResponse wrapper structure
      if (response && response.success) {
        setRequests(response.data.content || []);
        setTotalPages(response.data.totalPages || 1);
      } else {
        console.error('Failed to fetch requests:', response ? response.message : 'Unknown error');
        setRequests([]);
      }
    } catch (error) {
      console.error('Error fetching requests:', error);
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      'SUBMITTED': 'bg-blue-100 text-blue-800',
      'ASSIGNED': 'bg-yellow-100 text-yellow-800',
      'COLLECTION_SCHEDULED': 'bg-purple-100 text-purple-800',
      'OUT_FOR_COLLECTION': 'bg-orange-100 text-orange-800',
      'COLLECTED': 'bg-green-100 text-green-800',
      'RESOLVED': 'bg-gray-100 text-gray-800',
      'CANCELLED': 'bg-red-100 text-red-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading your requests...</p>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-8 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex justify-between items-center mb-6">
              <h1 className="text-2xl font-bold text-gray-800">Track Your Requests</h1>
            </div>

            {requests.length === 0 ? (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No requests found.</p>
                <button
                  onClick={() => navigate('/citizen/report-bin-request')}
                  className="mt-4 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
                >
                  Submit Your First Request
                </button>
              </div>
            ) : (
              <>
                {/* Requests Table */}
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Request ID
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Category
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Status
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Submitted Date
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                          Actions
                        </th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {requests.map((request) => (
                        <tr key={request.requestId} className="hover:bg-gray-50">
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {request.requestId}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {categoryDisplay[request.category] || request.category}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap">
                            <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(request.status)}`}>
                              {statusDisplay[request.status] || request.status}
                            </span>
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {request.submittedAt ? new Date(request.submittedAt).toLocaleDateString() : 'N/A'}
                          </td>
                          <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                            <button
                              onClick={() => navigate(`/citizen/request-details/${request.requestId}`)}
                              className="text-green-600 hover:text-green-900 mr-3"
                            >
                              View Details
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="flex justify-center items-center space-x-2 mt-6 pt-6 border-t border-gray-200">
                    <button
                      onClick={() => paginate(Math.max(0, currentPage - 1))}
                      disabled={currentPage === 0}
                      className="px-3 py-1 border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                    >
                      Previous
                    </button>
                    
                    {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                      let pageNumber;
                      if (totalPages <= 5) {
                        pageNumber = i;
                      } else if (currentPage <= 2) {
                        pageNumber = i;
                      } else if (currentPage >= totalPages - 3) {
                        pageNumber = totalPages - 5 + i;
                      } else {
                        pageNumber = currentPage - 2 + i;
                      }

                      return (
                        <button
                          key={pageNumber}
                          onClick={() => paginate(pageNumber)}
                          className={`px-3 py-1 border rounded ${
                            currentPage === pageNumber
                              ? 'bg-green-600 text-white border-green-600'
                              : 'border-gray-300 hover:bg-gray-50'
                          }`}
                        >
                          {pageNumber + 1}
                        </button>
                      );
                    })}

                    <button
                      onClick={() => paginate(Math.min(totalPages - 1, currentPage + 1))}
                      disabled={currentPage === totalPages - 1}
                      className="px-3 py-1 border border-gray-300 rounded disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                    >
                      Next
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default TrackRequests;