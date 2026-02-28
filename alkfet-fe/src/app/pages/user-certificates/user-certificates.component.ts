import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { UserCertificateService } from '../../services/user-certificate.service';
import { RootCertificateService } from '../../services/root-certificate.service';
import { UserCertificate, RootCertificate, PageResponse } from '../../models/certificate.model';

@Component({
  selector: 'app-user-certificates',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-certificates.component.html',
  styleUrl: './user-certificates.component.css'
})
export class UserCertificatesComponent implements OnInit {
  private readonly userService = inject(UserCertificateService);
  private readonly rootService = inject(RootCertificateService);
  private readonly fb = inject(FormBuilder);

  page = signal<PageResponse<UserCertificate>>({
    content: [], page: 0, size: 10,
    totalElements: 0, totalPages: 0, first: true, last: true
  });
  currentPage = signal(0);
  pageSize = signal(10);

  rootCertificates = signal<RootCertificate[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  showSignForm = signal(false);
  signing = signal(false);

  signForm = this.fb.group({
    rootCertificateId: ['', Validators.required],
    csrPem: ['', Validators.required],
    validityDays: [365, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.load();
    this.loadRootCerts();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.userService.list(this.currentPage(), this.pageSize()).subscribe({
      next: (data) => { this.page.set(data); this.loading.set(false); },
      error: () => { this.error.set('Nem sikerült betölteni a tanúsítványokat.'); this.loading.set(false); }
    });
  }

  goToPage(p: number): void {
    if (p < 0 || p >= this.page().totalPages) return;
    this.currentPage.set(p);
    this.load();
  }

  loadRootCerts(): void {
    // Root certeknél nem szükséges pagination a select listához, az összes kell
    this.rootService.list(0, 100).subscribe({
      next: (data) => this.rootCertificates.set(data.content),
      error: () => {}
    });
  }

  onCsrFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      this.signForm.patchValue({ csrPem: e.target?.result as string });
    };
    reader.readAsText(file);
  }

  onSign(): void {
    if (this.signForm.invalid) return;
    this.signing.set(true);
    this.userService.signAndStore(this.signForm.value as any).subscribe({
      next: () => {
        this.signForm.reset({ validityDays: 365 });
        this.showSignForm.set(false);
        this.signing.set(false);
        this.currentPage.set(0);
        this.load();
      },
      error: () => {
        this.error.set('Nem sikerült aláírni a CSR-t.');
        this.signing.set(false);
      }
    });
  }

  onDelete(cert: UserCertificate): void {
    if (!confirm(`Biztosan törli a "${cert.commonName}" tanúsítványt?`)) return;
    this.userService.delete(cert.id).subscribe({
      next: () => {
        const isLastOnPage = this.page().content.length === 1 && this.currentPage() > 0;
        this.currentPage.set(isLastOnPage ? this.currentPage() - 1 : this.currentPage());
        this.load();
      },
      error: () => this.error.set('Nem sikerült törölni a tanúsítványt.')
    });
  }

  onDownload(cert: UserCertificate): void {
    this.userService.downloadPem(cert);
  }

  statusLabel(status: string): string {
    return ({ ACTIVE: 'Aktív', REVOKED: 'Visszavont', EXPIRED: 'Lejárt' } as Record<string, string>)[status] ?? status;
  }

  statusClass(status: string): string {
    return ({ ACTIVE: 'badge-ok', REVOKED: 'badge-revoked', EXPIRED: 'badge-expired' } as Record<string, string>)[status] ?? '';
  }

  rootCertName(id: string): string {
    return this.rootCertificates().find(r => r.id === id)?.commonName ?? id;
  }

  get pages(): number[] {
    return Array.from({ length: this.page().totalPages }, (_, i) => i);
  }
}
