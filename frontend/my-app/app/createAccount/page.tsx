"use client"
import React, { useState } from 'react';
import HeaderPage from '../header/headerpage';
import { FaPlus } from 'react-icons/fa';
import axiosInstance from '../auth/auth.interceptor';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface AccountsDto {
  accountId: string; 
  accountType: string; 
  balance: number;
  userEmail: string;
}

const CreateAccountPage: React.FC = () => {
  const [accountId, setAccountId] = useState<string>(''); 
  const [accountType, setAccountType] = useState<string>('');
  const [balance, setBalance] = useState<number>(0);
  const [userEmail, setUserEmail] = useState<string>('');

  const handleCreateAccount = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const accountDto: AccountsDto = {
      accountId,
      accountType,
      balance,
      userEmail,
    };

    console.log(accountDto.accountId);



    axiosInstance.post('http://localhost:9000/api/accounts/register', accountDto)
      .then(() => {
        toast.success("Account created successfully");
      })
      .catch((error) => {
        toast.error("There was a problem with creating the account.")
      });
  };

  return (
    <div>
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">Create Account</h2>
          <form onSubmit={handleCreateAccount} className="space-y-4">
            <input
              type="text"
              placeholder="Account ID"
              required
              className="w-full p-2 border rounded"
              value={accountId}
              onChange={(e) => setAccountId(e.target.value)}
            />
            <select
              required
              className="w-full p-2 border rounded"
              value={accountType}
              onChange={(e) => setAccountType(e.target.value)}
            >
              <option value="" disabled>Select Account Type</option>
              <option value="SAVINGS">SAVINGS</option>
              <option value="CHECKING">CHECKING</option>
              <option value="BUSINESS">BUSINESS</option>
              {/* Add other account types as necessary */}
            </select>
            <input
              type="number"
              step="0.01"
              placeholder="Initial Balance"
              required
              className="w-full p-2 border rounded"
              value={balance}
              onChange={(e) => setBalance(parseFloat(e.target.value))}
            />
            <input
              type="email"
              placeholder="User Email"
              required
              className="w-full p-2 border rounded"
              value={userEmail}
              onChange={(e) => setUserEmail(e.target.value)}
            />
            <button
              type="submit"
              className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600 transition"
            >
              <FaPlus className="inline-block mr-2" />
              Create Account
            </button>
          </form>
        </div>
      </main>
    </div>
  )
}

export default CreateAccountPage;
