"use client";
import React, { useState, useEffect } from "react";
import { FaCreditCard, FaSearch } from "react-icons/fa";
import axiosInstance from "../auth/auth.interceptor";
import HeaderPage from "../header/headerpage";
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { loadStripe } from "@stripe/stripe-js";
import { Elements, CardElement, useStripe, useElements } from "@stripe/react-stripe-js";

const stripePromise = loadStripe("pk_test_51RProu4dWuzF8mxlJDJNSDjJZyDitdLEQ7HsKKTl4IRW58GPEopSp2bnlpIrPd6PO8CdeALI7LdH1G6xFHmk6avg00pvaNefxT");
console.log(stripePromise)

interface PaymentDto {
  paymentId?: string;
  source_accountId?: string;
  destination_accountId?: string;
  amount: number;
  paymentMethod?: string;
  status?: string;
}

interface PaymentCardDto {
  paymentDto: PaymentDto;
  paymentMethodId: string;
}

const PaymentForm: React.FC = () => {
  const stripe = useStripe();
  const elements = useElements();
  const [createdPaymentId, setCreatedPaymentId] = useState<string | null>(null);
  const [paymentDetails, setPaymentDetails] = useState<PaymentDto | null>(null);

  console.log("stripe:", stripe)
  console.log("elements:", elements)

  const handleCreate = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    console.log("stripe:", stripe)
    console.log("elements:", elements)
    if (!stripe || !elements) return;

    const formData = new FormData(event.currentTarget);
    const source_accountId = formData.get("source_accountId") as string;
    const destination_accountId = formData.get("destination_accountId") as string;
    const amount = parseFloat(formData.get("amount") as string);

    const cardElement = elements.getElement(CardElement);
    if (!cardElement) return;

    try {
      const { paymentMethod, error } = await stripe.createPaymentMethod({
        type: "card",
        card: cardElement,
      });
      if (error) {
        toast.error(error.message);
        return;
      }

      const paymentCardDto: any = {
        "@type": "PaymentCardDto",
        paymentDto: {
          "@type": "PaymentDto",
          source_accountId,
          destination_accountId,
          amount: ["java.math.BigDecimal", amount.toString()],
          paymentMethod: "CREDIT_CARD"
        },
        paymentMethodId: paymentMethod.id,
      };

      const response = await axiosInstance.post("http://localhost:9000/api/payment/create", paymentCardDto);
      setCreatedPaymentId(response.data.toString());
      toast.success("Payment created successfully!");
    } catch (error: any) {
      console.error("Error creating payment:", error);
      toast.error("There was a problem with creating the payment");
    }
  };

  const handleGet = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      const response = await axiosInstance.get(`http://localhost:9000/api/payment/${createdPaymentId}`);
      setPaymentDetails(response.data);
    } catch (error) {
      toast.error("There was a problem with fetching the payment information");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <HeaderPage />
      <main className="container mx-auto py-12 px-4">
        <ToastContainer />
        <div className="bg-white p-10 rounded-lg shadow-lg">
          <h2 className="text-3xl font-bold mb-8 text-gray-900">Manage Payments</h2>
          <section className="grid grid-cols-1 sm:grid-cols-2 gap-8">
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg">
              <FaCreditCard className="text-blue-500 text-6xl mb-6" />
              <h3 className="text-2xl font-semibold mb-4">Create Payment</h3>
              <form onSubmit={handleCreate} className="space-y-4">
                <input name="source_accountId" placeholder="Your Account ID" required className="w-full p-3 border rounded" />
                <input name="destination_accountId" placeholder="Destination Account ID" required className="w-full p-3 border rounded" />
                <input name="amount" type="number" step="0.01" placeholder="Amount" required className="w-full p-3 border rounded" />
                <CardElement className="p-3 border rounded" />
                <button type="submit" className="w-full bg-blue-500 text-white py-3 px-4 rounded-lg hover:bg-blue-600">
                  Create Payment
                </button>
              </form>
            </div>

            {/* Fetch Payment Details */}
            <div className="bg-blue-50 p-6 rounded-lg shadow-lg">
              <FaSearch className="text-blue-500 text-6xl mb-6" />
              <h3 className="text-2xl font-semibold mb-4">Fetch Payment Details</h3>
              <form onSubmit={handleGet} className="space-y-4">
                <button type="submit" className="w-full bg-blue-500 text-white py-3 px-4 rounded-lg hover:bg-blue-600">
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
                </div>
              )}
            </div>
          </section>
        </div>
      </main>
    </div>
  );
};

const PaymentPage: React.FC = () => {
  return (
    <Elements stripe={stripePromise}>
      <PaymentForm />
    </Elements>
  );
};

export default PaymentPage;
