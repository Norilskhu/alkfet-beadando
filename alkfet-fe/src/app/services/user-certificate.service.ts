import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  UserCertificate,
  SignUserCertificateRequest,
  DeleteCertificateResponse,
  PageResponse
} from '../models/certificate.model';

@Injectable({ providedIn: 'root' })
export class UserCertificateService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/user-certificates';

  list(page = 0, size = 10, rootCertificateId?: string): Observable<PageResponse<UserCertificate>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);
    if (rootCertificateId) {
      params = params.set('rootCertificateId', rootCertificateId);
    }
    return this.http.get<PageResponse<UserCertificate>>(this.baseUrl, { params });
  }

  getById(id: string): Observable<UserCertificate> {
    return this.http.get<UserCertificate>(`${this.baseUrl}/${id}`);
  }

  signAndStore(request: SignUserCertificateRequest): Observable<UserCertificate> {
    return this.http.post<UserCertificate>(this.baseUrl, request);
  }

  delete(id: string): Observable<DeleteCertificateResponse> {
    return this.http.delete<DeleteCertificateResponse>(`${this.baseUrl}/${id}`);
  }

  downloadPem(cert: UserCertificate): void {
    const blob = new Blob([cert.certificatePem], { type: 'application/x-pem-file' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${cert.commonName}-user.pem`;
    a.click();
    URL.revokeObjectURL(url);
  }
}

