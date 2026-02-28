import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  RootCertificate,
  CreateRootCertificateRequest,
  DeleteCertificateResponse,
  PageResponse
} from '../models/certificate.model';

@Injectable({ providedIn: 'root' })
export class RootCertificateService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/root-certificates';

  list(page = 0, size = 10): Observable<PageResponse<RootCertificate>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<PageResponse<RootCertificate>>(this.baseUrl, { params });
  }

  getById(id: string): Observable<RootCertificate> {
    return this.http.get<RootCertificate>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateRootCertificateRequest): Observable<RootCertificate> {
    return this.http.post<RootCertificate>(this.baseUrl, request);
  }

  delete(id: string): Observable<DeleteCertificateResponse> {
    return this.http.delete<DeleteCertificateResponse>(`${this.baseUrl}/${id}`);
  }

  downloadPem(cert: RootCertificate): void {
    const blob = new Blob([cert.certificatePem], { type: 'application/x-pem-file' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${cert.commonName}-root.pem`;
    a.click();
    URL.revokeObjectURL(url);
  }
}

