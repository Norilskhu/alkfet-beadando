import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RootCertificateService } from '../../services/root-certificate.service';
import { RootCertificate, PageResponse } from '../../models/certificate.model';

@Component({
  selector: 'app-root-certificates',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './root-certificates.component.html',
  styleUrl: './root-certificates.component.css'
})
export class RootCertificatesComponent implements OnInit {
  private readonly service = inject(RootCertificateService);
  private readonly fb = inject(FormBuilder);

  page = signal<PageResponse<RootCertificate>>({
    content: [], page: 0, size: 10,
    totalElements: 0, totalPages: 0, first: true, last: true
  });
  currentPage = signal(0);
  pageSize = signal(10);

  loading = signal(false);
  error = signal<string | null>(null);
  showCreateForm = signal(false);
  creating = signal(false);

  createForm = this.fb.group({
    commonName: ['', Validators.required],
    organization: ['', Validators.required],
    organizationalUnit: [''],
    country: ['HU', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
    state: [''],
    locality: [''],
    validityDays: [3650, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list(this.currentPage(), this.pageSize()).subscribe({
      next: (data) => {
        this.page.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Nem sikerült betölteni a tanúsítványokat.');
        this.loading.set(false);
      }
    });
  }

  goToPage(p: number): void {
    if (p < 0 || p >= this.page().totalPages) return;
    this.currentPage.set(p);
    this.load();
  }

  onCreate(): void {
    if (this.createForm.invalid) return;
    this.creating.set(true);
    this.service.create(this.createForm.value as any).subscribe({
      next: () => {
        this.createForm.reset({ country: 'HU', validityDays: 3650 });
        this.showCreateForm.set(false);
        this.creating.set(false);
        this.currentPage.set(0);
        this.load();
      },
      error: () => {
        this.error.set('Nem sikerült létrehozni a tanúsítványt.');
        this.creating.set(false);
      }
    });
  }

  onDelete(cert: RootCertificate): void {
    if (!confirm(`Biztosan törli a "${cert.commonName}" gyökér tanúsítványt?`)) return;
    this.service.delete(cert.id).subscribe({
      next: () => {
        // Ha az utolsó elem volt az oldalon, lépjünk vissza
        const isLastOnPage = this.page().content.length === 1 && this.currentPage() > 0;
        this.currentPage.set(isLastOnPage ? this.currentPage() - 1 : this.currentPage());
        this.load();
      },
      error: () => this.error.set('Nem sikerült törölni a tanúsítványt.')
    });
  }

  onDownload(cert: RootCertificate): void {
    this.service.downloadPem(cert);
  }

  isExpired(validTo: string): boolean {
    return new Date(validTo) < new Date();
  }

  get pages(): number[] {
    return Array.from({ length: this.page().totalPages }, (_, i) => i);
  }
}
