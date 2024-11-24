"use client";
import React, { useState, useEffect } from "react";
import { FaCreditCard, FaSearch } from "react-icons/fa";
import axiosInstance from "../auth/auth.interceptor";
import HeaderPage from "../header/headerpage";
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

interface PaymentDto {
  paymentId?: string;
  source_accountId?: string;
  destination_accountId?: string;
  amount: number;
  paymentMethod?: string;
  status?: string;
}

interface CardDto {
  cardNumber: string;
  expiryMonth: number;
  expiryYear: number;
  cvc: string;
}

interface PaymentCardDto {
  paymentDto: PaymentDto;
  cardDto: CardDto;
}

const PaymentPage: React.FC = () => {
  const [createdPaymentId, setCreatedPaymentId] = useState<string | null>(null);
  const [paymentDetails, setPaymentDetails] = useState<PaymentDto | null>(null);
  const [isClient, setIsClient] = useState<boolean>(false);

  useEffect(() => {
      setIsClient(typeof window !== 'undefined');
    }, []);

  const handleCreate = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);

    const paymentCardDto: PaymentCardDto = {
      paymentDto: {
        source_accountId: formData.get("source_accountId") as string,
        destination_accountId: formData.get("destination_accountId") as string,
        amount: parseFloat(formData.get("amount") as string),
        paymentMethod: String(formData.get("paymentMethod")),
      },
      cardDto: {
        cardNumber: formData.get("cardNumber") as string,
        expiryMonth: parseInt(formData.get("expiryMonth") as string, 10),
        expiryYear: parseInt(formData.get("expiryYear") as string, 10),
        cvc: formData.get("cvc") as string,
      },
    };

    axiosInstance.post("http://localhost:9000/api/payment/create", paymentCardDto)
    .then((res) => {
        const id = res.data.toString();
        setCreatedPaymentId(id);
        toast.success("Payment created successfully!");
      })
      .catch((error) => {
        console.error("Error creating payment:", error.response ? error.response.data : error.message);
        toast.error("There was a problem with creating the payment");
      });
  };

  const handleGet = (event: React.FormEvent) => {
    event.preventDefault();

    axiosInstance.get(`http://localhost:9000/api/payment/${createdPaymentId}`)
      .then((res) => {
        setPaymentDetails(res.data);
      })
      .catch((error) => {
        toast.error("There was a problem with fetching the payment information");
      });
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-10 rounded-lg shadow-lg">
          <h2 className="text-3xl font-bold mb-8 text-gray-900">Manage Payments</h2>
          <section className="grid grid-cols-1 sm:grid-cols-2 gap-8">
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaCreditCard className="text-blue-500 text-6xl mb-6" />
              <h3 className="text-2xl font-semibold mb-4">Create Payment</h3>
              <form onSubmit={handleCreate} className="space-y-4">
                <input name="source_accountId" placeholder="Your Account ID" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input name="destination_accountId" placeholder="Destination Account ID" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input name="amount" type="number" step="0.01" placeholder="Amount" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <select name="paymentMethod" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500">
                  <option value="" disabled>Select Payment Method</option>
                  <option value="CREDIT_CARD">CREDIT_CARD</option>
                  <option value="DEBIT_CARD">DEBIT_CARD</option>
                </select>
                <input name="cardNumber" placeholder="Card Number" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input name="expiryMonth" type="number" placeholder="Expiry Month" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input name="expiryYear" type="number" placeholder="Expiry Year" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <input name="cvc" placeholder="CVC" required className="w-full p-3 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500" />
                <button type="submit" className="w-full bg-blue-500 text-white py-3 px-4 rounded-lg hover:bg-blue-600 transition-colors duration-300">
                  Create Payment
                </button>
              </form>
            </div>

            {/* Fetch Payment Details Card */}
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg hover:bg-blue-100 transition-all">
              <FaSearch className="text-blue-500 text-6xl mb-6" />
              <h3 className="text-2xl font-semibold mb-4">Fetch Payment Details</h3>
              <form onSubmit={handleGet} className="space-y-4">
                <button type="submit" className="w-full bg-blue-500 text-white py-3 px-4 rounded-lg hover:bg-blue-600 transition-colors duration-300">
                  Fetch Payment Information
                </button>
              </form>

              {paymentDetails && (
                <div className="mt-6 bg-white p-6 rounded-lg shadow-inner">
                  <h4 className="text-lg font-semibold">Payment Details:</h4>
                  <p><strong>Your Account ID:</strong> {paymentDetails.source_accountId}</p>
                  <p><strong>Destination Account ID:</strong> {paymentDetails.destination_accountId}</p>
                  <p><strong>Amount:</strong> {paymentDetails.amount}</p>
                  <p><strong>Status:</strong> {paymentDetails.status}</p>
                  <p><strong>Payment Method:</strong> {paymentDetails.paymentMethod}</p>
                </div>
              )}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
};

export default PaymentPage;

