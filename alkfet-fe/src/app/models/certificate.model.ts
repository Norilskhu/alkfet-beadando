export interface RootCertificate {
  id: string;
  commonName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  state: string;
  locality: string;
  validFrom: string;
  validTo: string;
  certificatePem: string;
  serialNumber: string;
  createdAt: string;
}

export interface CreateRootCertificateRequest {
  commonName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  state: string;
  locality: string;
  validityDays: number;
}

export interface UserCertificate {
  id: string;
  rootCertificateId: string;
  commonName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  state: string;
  locality: string;
  validFrom: string;
  validTo: string;
  certificatePem: string;
  csrPem: string;
  serialNumber: string;
  status: 'ACTIVE' | 'REVOKED' | 'EXPIRED';
  storedAt: string;
  createdAt: string;
}

export interface SignUserCertificateRequest {
  rootCertificateId: string;
  csrPem: string;
  validityDays: number;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface DeleteCertificateResponse {
  id: string;
  message: string;
  success: boolean;
}

