"use client";

import React, { useEffect, useState } from 'react';
import { FaEdit, FaTrashAlt, FaSearch, FaTimes, FaSyncAlt } from 'react-icons/fa';
import axiosInstance from '../auth/auth.interceptor';
import HeaderPage from '../header/headerpage';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface AccountsDto {
  accountId: string;
  accountType: string;
  balance: number;
  userEmail: string;
  status: string;
}

const ManageAccountPage: React.FC = () => {
  const [accounts, setAccounts] = useState<AccountsDto[]>([]);
  const [selectedAccountId, setSelectedAccountId] = useState<string | null>(null);
  const [accountDetails, setAccountDetails] = useState<Partial<AccountsDto>>({});
  const [deleteAccountId, setDeleteAccountId] = useState<string>('');
  const [getAccountId, setGetAccountId] = useState<string>('');
  const [closeAccountId, setCloseAccountId] = useState<string>('');
  const [statusAccountId, setStatusAccountId] = useState<string>('');
  const [status, setStatus] = useState<string>('');
  const [notification, setNotification] = useState<string>('');

  useEffect(() => {
      if (typeof window !== 'undefined') {
         axiosInstance
          .get("http://localhost:9000/api/accounts/allaccounts")
          .then((response) => setAccounts(response.data))
          .catch((error) => toast.error("There was a problem fetching the accounts."));
      }
  }, []);

  const handleUpdateAccount = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedAccountId) {
      toast.error("Please enter a valid account ID first.");
      return;
    }

    const accountDto: AccountsDto = {
      accountId: selectedAccountId,
      accountType: accountDetails.accountType || "",
      balance: accountDetails.balance || 0,
      userEmail: accountDetails.userEmail || "",
      status: accountDetails.status || "",
    };

    axiosInstance
      .put(`http://localhost:9000/api/accounts/${selectedAccountId}`, accountDto)
      .then(() => toast.success("Account updated successfully!"))
      .catch(() => toast.error("There was a problem updating the account."));
  };

  const handleDeleteAccount = (event: React.FormEvent) => {
    event.preventDefault();
    if (!deleteAccountId) {
      toast.error("Please enter a valid account ID first.");
      return;
    }

    axiosInstance
      .delete(`http://localhost:9000/api/accounts/${deleteAccountId}`)
      .then(() => {
        toast.success("Account deleted successfully!");
        setDeleteAccountId("");
      })
      .catch(() => toast.error("There was a problem deleting the account."));
  };

  const handleGetAccount = (event: React.FormEvent) => {
    event.preventDefault();
    axiosInstance
      .get(`http://localhost:9000/api/accounts/${getAccountId}`)
      .then((response) => {
        setAccountDetails(response.data);
      })
      .catch(() => toast.error("There was a problem getting the account information."));
  };

  const handleCloseAccount = (event: React.FormEvent) => {
    event.preventDefault();

    axiosInstance
      .put(`http://localhost:9000/api/accounts/close/${closeAccountId}`, closeAccountId)
      .then(() => {
        setCloseAccountId("");
        toast.success("Account closed successfully!");
      })
      .catch(() => toast.error("There was a problem closing the account."));
  };

  const handleUpdateStatus = (event: React.FormEvent) => {
    event.preventDefault();

    axiosInstance
      .put(`http://localhost:9000/api/accounts/updateStatus/${statusAccountId}`, { status })
      .then(() => {
        toast.success("Status updated successfully!");
        setStatusAccountId("");
        setStatus("");
      })
      .catch(() => toast.error("There was a problem updating the status."));
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Manage Accounts</h2>
          <section className="grid grid-cols-1 sm:grid-cols-2 gap-8">
            <div className="bg-yellow-50 p-6 rounded-lg shadow-lg hover:bg-yellow-100 transition-all">
              <FaEdit className="text-yellow-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Update Account</h2>
              <form onSubmit={handleUpdateAccount} className="space-y-4">
                <input
                  type="text"
                  name="accountId"
                  placeholder="Enter Account ID"
                  value={selectedAccountId || ''}
                  onChange={(e) => {
                    setSelectedAccountId(e.target.value);
                    setAccountDetails({});
                  }}
                  required
                  className="w-full p-2 border rounded"
                />
                <select
                  name="accountType"
                  required
                  className="w-full p-2 border rounded"
                  value={accountDetails.accountType || ''}
                  onChange={(e) => setAccountDetails({ ...accountDetails, accountType: e.target.value })}
                >
                  <option value="" disabled>Select Account Type</option>
                  <option value="SAVINGS">SAVINGS</option>
                  <option value="CHECKING">CHECKING</option>
                  <option value="BUSINESS">BUSINESS</option>
                </select>
                <select
                  name="status"
                  required
                  className="w-full p-2 border rounded"
                  value={accountDetails.status || ''}
                  onChange={(e) => setAccountDetails({ ...accountDetails, status: e.target.value })}
                >
                  <option value="" disabled>Select Status</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="INACTIVE">INACTIVE</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
                <input
                  name="balance"
                  placeholder="Update Balance"
                  type="number"
                  value={accountDetails.balance || ''}
                  onChange={(e) => setAccountDetails({ ...accountDetails, balance: Number(e.target.value) })}
                  required
                  className="w-full p-2 border rounded"
                />
                <input
                  name="userEmail"
                  placeholder="Update User Email"
                  value={accountDetails.userEmail || ''}
                  onChange={(e) => setAccountDetails({ ...accountDetails, userEmail: e.target.value })}
                  className="w-full p-2 border rounded"
                />
                <button
                  type="submit"
                  className="bg-yellow-500 text-white py-2 px-4 rounded-lg hover:bg-yellow-600 transition"
                >
                  Update Account
                </button>
              </form>
            </div>
            <div className="bg-red-50 p-6 rounded-lg shadow-lg hover:bg-red-100 transition-all">
              <FaTrashAlt className="text-red-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Delete Account</h2>
              <form onSubmit={handleDeleteAccount} className="space-y-4">
                <input
                  type="text"
                  name="DeleteAccountId"
                  placeholder="Confirm Account ID (to delete)"
                  value={deleteAccountId}
                  onChange={(e) => setDeleteAccountId(e.target.value)}
                  required
                  className="w-full p-2 border rounded"
                />
                <button
                  type="submit"
                  className="bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition"
                >
                  Delete Account
                </button>
              </form>
            </div>
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaSearch className="text-blue-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Fetch Account Details</h2>
                <input
                type="text"
                name="getAccountId"
                placeholder="Enter Account ID"
                value={getAccountId || ''}
                onChange={(e) => setGetAccountId(e.target.value)}
                required
                className="w-full p-2 border rounded mb-4"
                />
                <button
                 onClick={handleGetAccount}
                 className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition"
                >
                  click here to see your Account information
                </button>

                {accountDetails.accountId && (
                  <div className="mt-6">
                   <h3 className="text-lg font-semibold">Account Details:</h3>
                   <p><strong>Account ID:</strong> {accountDetails.accountId}</p>
                   <p><strong>Account Type:</strong> {accountDetails.accountType}</p>
                   <p><strong>Balance:</strong> ${accountDetails.balance}</p>
                   <p><strong>User Email:</strong> {accountDetails.userEmail}</p>
                   <p><strong>Status:</strong> {accountDetails.status}</p>
                  </div>
                  )}
            </div>
            <div className="bg-gray-50 p-6 rounded-lg shadow-lg hover:bg-gray-100 transition-all">
              <FaTimes className="text-gray-500 text-5xl mb-4" />
              <h2 className="text-xl font-semibold mb-2">Close Account</h2>
              <form onSubmit={handleCloseAccount} className="space-y-4">
                <input
                  type="text"
                  name="closeAccountId"
                  placeholder="Enter Account ID to Close"
                  value={closeAccountId}
                  onChange={(e) => setCloseAccountId(e.target.value)}
                  required
                  className="w-full p-2 border rounded"
                />
                <button
                  type="submit"
                  className="bg-gray-500 text-white py-2 px-4 rounded-lg hover:bg-gray-600 transition"
                >
                  Close Account
                </button>
              </form>
            </div>
            <div className="bg-green-50 p-6 rounded-lg shadow-lg hover:bg-green-100 transition-all">
             <FaSyncAlt className="text-green-500 text-5xl mb-4" />
             <h2 className="text-xl font-semibold mb-2">Update Account Status</h2>
             <form onSubmit={handleUpdateStatus} className="space-y-4">
              <input
               type="text"
               placeholder="Enter Account ID"
               value={statusAccountId}
               onChange={(e) => setStatusAccountId(e.target.value)}
               required
               className="w-full p-2 border rounded"
              />
              <select
               value={status}
               onChange={(e) => setStatus(e.target.value)}
               required
               className="w-full p-2 border rounded"
              >
               <option value="" disabled>Select Status</option>
               <option value="ACTIVE">ACTIVE</option>
               <option value="INACTIVE">INACTIVE</option>
               <option value="CLOSED">CLOSED</option>
              </select>
              <button
               type="submit"
               className="bg-green-500 text-white py-2 px-4 rounded-lg hover:bg-green-600 transition"
              >
          Update Status
        </button>
      </form>
    </div>
          </section> 
        </div>
      </main>
    </div>
  );
};

export default ManageAccountPage;
