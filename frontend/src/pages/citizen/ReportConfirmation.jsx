import { useLocation, useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';

function ReportConfirmation() {
  const location = useLocation();
  const navigate = useNavigate();
  const requestData = location.state?.requestData;

  if (!requestData) {
  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      <main className="flex-grow bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-500 text-lg">No request data found.</p>
          <button
            onClick={() => navigate('/citizen-dashboard')}
            className="mt-4 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
          >
            Back to Dashboard
          </button>
        </div>
      </main>
      <AuthFooter />
    </div>
  );
}

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-12 px-4">
        <div className="container mx-auto max-w-2xl">
          <div className="bg-white rounded-lg shadow-md p-8 text-center">
            {/* Success Icon */}
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>

            <h1 className="text-3xl font-bold text-gray-800 mb-4">Request Submitted Successfully!</h1>
            <p className="text-gray-600 mb-8">
              Your waste disposal request has been received and is being processed.
            </p>

            {/* Request Summary */}
            <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-8 text-left">
              <h2 className="text-xl font-semibold text-green-800 mb-4">Request Summary</h2>
              
              <div className="grid md:grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Request ID:</span>
                  <p className="text-gray-900 font-mono">{requestData.requestId}</p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Category:</span>
                  <p className="text-gray-900">{requestData.category}</p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Location:</span>
                  <p className="text-gray-900">{requestData.location?.address}</p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Submitted:</span>
                  <p className="text-gray-900">
                    {new Date(requestData.submittedAt).toLocaleString()}
                  </p>
                </div>
                <div className="md:col-span-2">
                  <span className="font-medium text-gray-700">Description:</span>
                  <p className="text-gray-900 mt-1">{requestData.description}</p>
                </div>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <button
                onClick={() => navigate('/citizen/track-requests')}
                className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition font-medium"
              >
                Track Your Requests
              </button>
              <button
                onClick={() => navigate('/citizen-dashboard')}
                className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-medium"
              >
                Back to Dashboard
              </button>
              <button
                onClick={() => navigate('/citizen/report-bin-request')}
                className="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-medium"
              >
                Submit Another Request
              </button>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default ReportConfirmation;