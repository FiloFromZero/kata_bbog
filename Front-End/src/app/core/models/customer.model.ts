export interface Customer {
  id: string;
  name: string;
  email: string;
  createdAt: string;
}

export interface LoginResponse {
  token: string;
}

export interface CustomerRequest {
  name: string;
  email: string;
}
