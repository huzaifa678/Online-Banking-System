"use client";
import React, { useState } from 'react';
import { FaMoneyBillWave, FaSearch } from 'react-icons/fa';
import axiosInstance from '../auth/auth.interceptor';
import HeaderPage from '../header/headerpage';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface TransactionDto {
  transactionId?: string;
  source_accountId: string;
  destination_accountId?: string;
  amount: number;
  transactionStatus?: string;
  transactionType: string;
}

const TransactionPage: React.FC = () => {
  const [transactionType, setTransactionType] = useState<string>('');
  const [createdTransactionId, setCreatedTransactionId] = useState<string | null>(null);
  const [transactionDetails, setTransactionDetails] = useState<TransactionDto | null>(null);

  const handleCreate = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const transactionDto: TransactionDto = {
      source_accountId: formData.get('source_accountId') as string,
      amount: parseFloat(formData.get('amount') as string),
      transactionType: transactionType,
    };

    if (transactionType === 'TRANSFER') {
      transactionDto.destination_accountId = formData.get('destination_accountId') as string;
    }

    axiosInstance.post('http://localhost:9000/api/transaction/create', transactionDto)
      .then((res) => {
        const id = res.data.toString();
        console.log('Transaction created with ID:', id);
        setCreatedTransactionId(id);
        toast.success('Transaction created successfully!');
      })
      .catch((error) => {
        toast.error("There was a problem with creating the transaction");
      });
  };

  const handleGet = (event: React.FormEvent) => {
    event.preventDefault();
    axiosInstance.get(`http://localhost:9000/api/transaction/${createdTransactionId}`)
      .then((res) => {
        setTransactionDetails(res.data);
      })
      .catch((error) => {
        toast.error("There was a problem with fetching the transaction");
      });
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Manage Transactions</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaMoneyBillWave className="text-blue-500 text-5xl mb-4" />
              <h3 className="text-xl font-semibold mb-4 text-gray-700">Create Transaction</h3>
              <form onSubmit={handleCreate} className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Source Account ID</label>
                  <input
                    name="source_accountId"
                    placeholder="Your Account ID"
                    required
                    className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                {transactionType === 'TRANSFER' && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">Destination Account ID</label>
                    <input
                      name="destination_accountId"
                      placeholder="Destination Account ID"
                      required
                      className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Amount</label>
                  <input
                    name="amount"
                    type="number"
                    step="0.01"
                    placeholder="Amount"
                    required
                    className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Transaction Type</label>
                  <select
                    name="transactionType"
                    required
                    className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    value={transactionType}
                    onChange={(e) => setTransactionType(e.target.value)}
                  >
                    <option value="" disabled>
                      Select Transaction Type
                    </option>
                    <option value="TRANSFER">TRANSFER</option>
                    <option value="WITHDRAWAL">WITHDRAWAL</option>
                    <option value="DEPOSIT">DEPOSIT</option>
                  </select>
                </div>

                <button
                  type="submit"
                  className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition"
                >
                  Create Transaction
                </button>
              </form>
            </div>

            {/* Fetch Transaction Details Section */}
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaSearch className="text-blue-500 text-5xl mb-4" />
              <h3 className="text-xl font-semibold mb-4 text-gray-700">Fetch Transaction Details</h3>
              <form onSubmit={handleGet} className="space-y-6">
                <button
                  type="submit"
                  className="w-full bg-blue-500 text-white py-3 rounded-lg hover:bg-blue-600 transition"
                >
                  Fetch Transaction Info
                </button>
              </form>

              {transactionDetails && (
                <div className="mt-6 space-y-2">
                  <h4 className="text-lg font-semibold text-gray-800">Transaction Details:</h4>
                  <p><strong>Your Account ID:</strong> {transactionDetails.source_accountId}</p>
                  {transactionDetails.destination_accountId && (
                    <p><strong>Destination Account ID:</strong> {transactionDetails.destination_accountId}</p>
                  )}
                  <p><strong>Amount:</strong> ${transactionDetails.amount.toFixed(2)}</p>
                  <p><strong>Status:</strong> {transactionDetails.transactionStatus || 'Pending'}</p>
                  <p><strong>Transaction Type:</strong> {transactionDetails.transactionType}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default TransactionPage;


