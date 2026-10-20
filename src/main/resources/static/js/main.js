/*
  AI-Powered Placement Management System
  Enterprise Frontend Application Orchestrator
  Technology: Vanilla JavaScript (ES6+), Thymeleaf-compatible
  Strict Rule: Zero Emojis
*/

function initAllModules() {
  initMobileNav();
  initAlertDismissal();
  initToastSystem();
  initUniversalPortalSwitcher();
  initStudentJobFlows();
  initStudentJobsFilter();
  initStudentApplicationsFilter();
  initStudentProfileActions();
  initStudentFeedbackForm();
  initRecruiterApplicantsInteractivity();
  initAdminManagementActions();
  initAdminStudentsFilter();
  initAdminApplicationsFilter();
  initAdminJobsFilter();
  initAdminFeedbackFilter();
  initUrlParamNotifications();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initAllModules);
} else {
  initAllModules();
}

/* -------------------------------------------------------------
   1. Mobile Navigation & Drawer Toggle
------------------------------------------------------------- */
function initMobileNav() {
  var toggleBtn = document.getElementById('sidebarToggleBtn');
  var sidebar = document.getElementById('appSidebar');
  var backdrop = document.getElementById('sidebarBackdrop');

  if (toggleBtn && sidebar && backdrop) {
    toggleBtn.addEventListener('click', function () {
      sidebar.classList.toggle('sidebar-open');
      backdrop.classList.toggle('active');
    });

    backdrop.addEventListener('click', function () {
      sidebar.classList.remove('sidebar-open');
      backdrop.classList.remove('active');
    });
  }
}

/* -------------------------------------------------------------
   2. Dismissible Alerts
------------------------------------------------------------- */
function initAlertDismissal() {
  document.addEventListener('click', function (e) {
    var dismissBtn = e.target.closest('.alert-dismiss-btn');
    if (dismissBtn) {
      var alertBox = dismissBtn.closest('.alert');
      if (alertBox) {
        alertBox.style.display = 'none';
      }
    }
  });
}

/* -------------------------------------------------------------
   3. Global Toast Notification System
------------------------------------------------------------- */
function initToastSystem() {
  var container = document.getElementById('portalToastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'portalToastContainer';
    container.className = 'portal-toast-container';
    document.body.appendChild(container);
  }

  window.showToast = function (message, type, duration) {
    type = type || 'info';
    duration = duration || 3500;

    var toast = document.createElement('div');
    toast.className = 'portal-toast portal-toast-' + type;

    var textSpan = document.createElement('span');
    textSpan.textContent = message;
    toast.appendChild(textSpan);

    container.appendChild(toast);

    // Trigger animation
    requestAnimationFrame(function () {
      toast.classList.add('active');
    });

    setTimeout(function () {
      toast.classList.remove('active');
      setTimeout(function () {
        if (toast.parentNode) {
          toast.parentNode.removeChild(toast);
        }
      }, 300);
    }, duration);
  };
}

/* -------------------------------------------------------------
   4. Universal Floating Persona & Portal Switcher Widget
------------------------------------------------------------- */
function initUniversalPortalSwitcher() {
  var existingWidget = document.getElementById('demoSwitcherWidget');
  if (existingWidget) return;

  var currentPath = window.location.pathname.toLowerCase();
  var currentRole = 'Portal';
  if (currentPath.indexOf('/student') !== -1) currentRole = 'Student';
  else if (currentPath.indexOf('/recruiter') !== -1) currentRole = 'Recruiter';
  else if (currentPath.indexOf('/admin') !== -1) currentRole = 'Admin';
  else if (currentPath.indexOf('/login') !== -1 || currentPath.indexOf('/register') !== -1) currentRole = 'Auth';
  else if (currentPath.indexOf('/design-system') !== -1) currentRole = 'Design System';

  var widget = document.createElement('div');
  widget.id = 'demoSwitcherWidget';
  widget.className = 'demo-switcher-widget';

  var currentNameEl = document.querySelector('.user-display-name') || document.querySelector('.sidebar-user-name');
  var currentName = (currentNameEl && currentNameEl.textContent.trim()) ? currentNameEl.textContent.trim() : '';

  var studentDesc = (currentRole === 'Student' && currentName) ? (currentName + ' &bull; Active') : 'Candidate Session';
  var recruiterDesc = (currentRole === 'Recruiter' && currentName) ? (currentName + ' &bull; Active') : 'Corporate Talent Partner';

  widget.innerHTML = [
    '<div class="demo-switcher-menu" id="demoSwitcherMenu">',
    '  <div class="demo-switcher-header">Switch Portal Persona</div>',
    '  <a href="/student/dashboard" class="demo-switcher-item ' + (currentRole === 'Student' ? 'current' : '') + '">',
    '    <div>',
    '      <div>Student Portal</div>',
    '      <div class="demo-switcher-item-desc">' + studentDesc + '</div>',
    '    </div>',
    '    <span class="badge badge-selected">Student</span>',
    '  </a>',
    '  <a href="/recruiter/dashboard" class="demo-switcher-item ' + (currentRole === 'Recruiter' ? 'current' : '') + '">',
    '    <div>',
    '      <div>Recruiter Portal</div>',
    '      <div class="demo-switcher-item-desc">' + recruiterDesc + '</div>',
    '    </div>',
    '    <span class="badge badge-shortlisted">Recruiter</span>',
    '  </a>',
    '  <a href="/admin/dashboard" class="demo-switcher-item ' + (currentRole === 'Admin' ? 'current' : '') + '">',
    '    <div>',
    '      <div>Admin Portal</div>',
    '      <div class="demo-switcher-item-desc">Dr. K. Ramanathan &bull; Dean T&amp;P</div>',
    '    </div>',
    '    <span class="badge badge-in-process">Admin</span>',
    '  </a>',
    '  <div style="border-top: 1px solid var(--color-border); margin: 4px 0;"></div>',
    '  <a href="/" class="demo-switcher-item ' + (currentPath === '/' || currentPath === '' ? 'current' : '') + '">',
    '    <div>',
    '      <div>University Gateway</div>',
    '      <div class="demo-switcher-item-desc">Public Placement Home</div>',
    '    </div>',
    '    <span class="tag">Home</span>',
    '  </a>',
    '  <a href="/design-system" class="demo-switcher-item ' + (currentRole === 'Design System' ? 'current' : '') + '">',
    '    <div>',
    '      <div>Design System</div>',
    '      <div class="demo-switcher-item-desc">UI Tokens &amp; Component Kit</div>',
    '    </div>',
    '    <span class="tag">Specs</span>',
    '  </a>',
    '  <a href="/login" class="demo-switcher-item ' + (currentRole === 'Auth' ? 'current' : '') + '">',
    '    <div>',
    '      <div>Sign In / Out</div>',
    '      <div class="demo-switcher-item-desc">Authentication Gateway</div>',
    '    </div>',
    '    <span class="tag">Auth</span>',
    '  </a>',
    '</div>',
    '<button type="button" class="demo-switcher-btn" id="demoSwitcherBtn" title="Switch Portal Persona">',
    '  <span>Portals</span>',
    '  <span class="badge-pill">' + currentRole + '</span>',
    '</button>'
  ].join('');

  document.body.appendChild(widget);

  var btn = document.getElementById('demoSwitcherBtn');
  var menu = document.getElementById('demoSwitcherMenu');

  if (btn && menu) {
    btn.addEventListener('click', function (e) {
      e.stopPropagation();
      menu.classList.toggle('active');
    });

    document.addEventListener('click', function (e) {
      if (!widget.contains(e.target)) {
        menu.classList.remove('active');
      }
    });
  }
}

/* -------------------------------------------------------------
   5. Student Job Application Flow (job-details.html)
------------------------------------------------------------- */
function initStudentJobFlows() {
  var applyBtn = document.getElementById('applyButton');
  if (!applyBtn) return;

  applyBtn.addEventListener('click', function () {
    showApplicationModal();
  });
}

function showApplicationModal() {
  var existingModal = document.getElementById('applicationModal');
  if (existingModal) {
    existingModal.classList.add('active');
    return;
  }

  var modal = document.createElement('div');
  modal.id = 'applicationModal';
  modal.className = 'portal-modal-overlay active';

    var candNameEl = document.querySelector('.user-display-name') || document.querySelector('.sidebar-user-name');
    var candRollEl = document.querySelector('.user-display-role');
    var candidateName = (candNameEl && candNameEl.textContent.trim()) ? candNameEl.textContent.trim() : 'Candidate';
    var candidateRoll = (candRollEl && candRollEl.textContent.trim()) ? candRollEl.textContent.trim() : 'Roll: Active';
    var resumeDocName = candidateName.replace(/\s+/g, '_') + '_Resume.pdf';

    modal.innerHTML = [
    '<div class="portal-modal-container">',
    '  <div class="portal-modal-header">',
    '    <h3 class="portal-modal-title">Confirm Campus Drive Application</h3>',
    '    <button type="button" class="portal-modal-close-btn" id="closeAppModalBtn">&times;</button>',
    '  </div>',
    '  <div class="portal-modal-body">',
    '    <div style="background: var(--color-bg); padding: var(--space-4); border-radius: var(--radius-md); margin-bottom: var(--space-4); border: 1px solid var(--color-border);">',
    '      <div style="font-weight: 700; font-size: var(--font-size-base); margin-bottom: 4px;">Software Engineer — Microsoft Corporation</div>',
    '      <div style="font-size: var(--font-size-xs); color: var(--color-text-secondary); margin-bottom: 8px;">Package: 24.5 LPA &bull; Drive Ref: MSFT-CRD-041</div>',
    '      <div class="badge badge-selected">AI Skill Match: 94%</div>',
    '    </div>',
    '    <div style="font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-bottom: var(--space-4);">',
    '      <strong>Candidate Details:</strong>',
    '      <ul style="list-style-type: disc; padding-left: 20px; margin-top: 6px; font-size: var(--font-size-xs); line-height: 1.6;">',
    '        <li>' + candidateName + ' &bull; ' + candidateRoll + '</li>',
    '        <li>Department: Computer Science &amp; Engineering</li>',
    '        <li>Resume: ' + resumeDocName + ' (Verified by T&amp;P)</li>',
    '      </ul>',
    '    </div>',
    '    <div class="alert alert-info" style="font-size: var(--font-size-xs); margin-bottom: 0;">',
    '      By applying, your verified academic credentials and resume will be transmitted directly to the Microsoft University Recruiting Team.',
    '    </div>',
    '  </div>',
    '  <div class="portal-modal-footer">',
    '    <button type="button" class="btn btn-secondary" id="cancelAppModalBtn">Cancel</button>',
    '    <button type="button" class="btn btn-primary" id="confirmAppModalBtn">Submit Application</button>',
    '  </div>',
    '</div>'
  ].join('');

  document.body.appendChild(modal);

  function closeModal() {
    modal.classList.remove('active');
  }

  document.getElementById('closeAppModalBtn').addEventListener('click', closeModal);
  document.getElementById('cancelAppModalBtn').addEventListener('click', closeModal);

  document.getElementById('confirmAppModalBtn').addEventListener('click', function () {
    closeModal();
    var applyBtn = document.getElementById('applyButton');
    if (applyBtn) {
      applyBtn.textContent = 'Application Submitted';
      applyBtn.className = 'btn btn-secondary btn-lg';
      applyBtn.disabled = true;
    }
    window.showToast('Application submitted successfully for Microsoft Corporation! Redirecting to applications...', 'success', 3000);
    setTimeout(function () {
      window.location.href = '/student/applications?applied=true';
    }, 1500);
  });
}

/* -------------------------------------------------------------
   6. Student Jobs Search & Multi-Criteria Live Filter (jobs.html)
------------------------------------------------------------- */
function initStudentJobsFilter() {
  var searchInput = document.getElementById('searchKeyword');
  var deptSelect = document.getElementById('filterDept') || document.getElementById('filterDepartment');
  var typeSelect = document.getElementById('filterType');
  var cgpaSelect = document.getElementById('filterMinCgpa');
  var locInput = document.getElementById('filterLocation');
  var applyBtn = document.getElementById('applyFiltersBtn');
  var resetBtn = document.getElementById('resetFiltersBtn');
  var form = document.getElementById('jobSearchFilterForm');

  function filterCards() {
    var jobCards = document.querySelectorAll('.student-job-card, #studentJobsGrid .card');
    if (!jobCards || jobCards.length === 0) return;

    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var typeVal = (typeSelect ? typeSelect.value : '').toLowerCase().trim();
    var cgpaVal = cgpaSelect && cgpaSelect.value ? parseFloat(cgpaSelect.value) : 0;
    var locVal = (locInput ? locInput.value : '').toLowerCase().trim();

    // Clean normalized string for robust matching across spaces/hyphens
    var normTypeVal = typeVal.replace(/[-\s_]/g, '');

    var visibleCount = 0;

    jobCards.forEach(function (card) {
      if (card.id === 'noMatchingJobsAlert') return; // Ignore empty state card

      var text = card.textContent.toLowerCase();
      var cardDept = (card.getAttribute('data-dept') || card.getAttribute('data-department') || '').toUpperCase();
      var cardType = (card.getAttribute('data-type') || '').toLowerCase();
      var normCardType = cardType.replace(/[-\s_]/g, '');
      var cardCgpaAttr = card.getAttribute('data-cgpa');
      var cardLoc = (card.getAttribute('data-location') || '').toLowerCase();

      // 1. Text keyword search
      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      // 2. Department match
      var matchDept = !deptVal || deptVal === 'ALL' ||
                      cardDept.indexOf('ALL') !== -1 ||
                      cardDept.indexOf(deptVal) !== -1 ||
                      (typeof matchDeptHelper === 'function' && matchDeptHelper(cardDept, deptVal)) ||
                      text.indexOf(deptVal.toLowerCase()) !== -1;

      // 3. Job type match (handles 'Full-time', 'Full Time', 'Internship')
      var matchType = !typeVal ||
                      normCardType.indexOf(normTypeVal) !== -1 ||
                      cardType.indexOf(typeVal) !== -1 ||
                      text.replace(/[-\s_]/g, '').indexOf(normTypeVal) !== -1;

      // 4. Location match
      var matchLoc = !locVal || cardLoc.indexOf(locVal) !== -1 || text.indexOf(locVal) !== -1;

      // 5. CGPA Benchmark match: student benchmark qualifies if job requirement <= benchmark
      var matchCgpa = true;
      if (cgpaVal > 0) {
        var cardCgpa = cardCgpaAttr ? parseFloat(cardCgpaAttr) : 0;
        if (!cardCgpa) {
          var cgpaMatch = text.match(/cgpa:?\s*([0-9.]+)/i);
          if (cgpaMatch) cardCgpa = parseFloat(cgpaMatch[1]);
        }
        if (cardCgpa && cardCgpa > cgpaVal) {
          matchCgpa = false;
        }
      }

      if (matchSearch && matchDept && matchType && matchLoc && matchCgpa) {
        card.style.display = '';
        visibleCount++;
      } else {
        card.style.display = 'none';
      }
    });

    // Update count badge if present
    var countBadge = document.getElementById('jobResultsCount');
    if (countBadge) {
      countBadge.textContent = 'Showing ' + visibleCount + ' Available Drive' + (visibleCount === 1 ? '' : 's');
      countBadge.style.display = '';
    }

    // Toggle empty state card
    var emptyState = document.getElementById('noMatchingJobsAlert');
    if (emptyState) {
      emptyState.style.display = (visibleCount === 0) ? 'block' : 'none';
    }

    // Toast feedback
    if (window.showToast) {
      if (searchVal || (deptVal && deptVal !== 'ALL') || typeVal || cgpaVal > 0 || locVal) {
        window.showToast('Filters applied: ' + visibleCount + ' campus drive' + (visibleCount === 1 ? '' : 's') + ' found.', 'info', 2000);
      }
    }
  }

  function resetFilters() {
    if (searchInput) searchInput.value = '';
    if (deptSelect) deptSelect.value = '';
    if (typeSelect) typeSelect.value = '';
    if (cgpaSelect) cgpaSelect.value = '';
    if (locInput) locInput.value = '';
    filterCards();
    if (window.showToast) {
      window.showToast('Filters reset. Showing all available campus placement drives.', 'info', 2000);
    }
  }

  // Expose on window for direct HTML inline click fallbacks
  window.filterStudentJobs = filterCards;
  window.resetStudentJobs = resetFilters;

  if (searchInput) {
    searchInput.addEventListener('input', filterCards);
    searchInput.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        filterCards();
      }
    });
  }
  if (deptSelect) deptSelect.addEventListener('change', filterCards);
  if (typeSelect) typeSelect.addEventListener('change', filterCards);
  if (cgpaSelect) cgpaSelect.addEventListener('change', filterCards);
  if (locInput) {
    locInput.addEventListener('input', filterCards);
    locInput.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') {
        e.preventDefault();
        filterCards();
      }
    });
  }
  if (applyBtn) {
    applyBtn.addEventListener('click', function (e) {
      e.preventDefault();
      filterCards();
    });
  }
  if (resetBtn) {
    resetBtn.addEventListener('click', function (e) {
      e.preventDefault();
      resetFilters();
    });
  }
  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      filterCards();
    });
  }
}

/* -------------------------------------------------------------
   7. Student Applications Filter Tabs (applications.html)
------------------------------------------------------------- */
function initStudentApplicationsFilter() {
  var table = document.querySelector('.table tbody');
  var filterSelect = document.getElementById('appStatusFilter');
  var filterBtn = document.getElementById('studentAppFilterBtn');
  var resetBtn = document.getElementById('studentAppResetBtn');
  if (!table) return;

  var rows = table.querySelectorAll('tr');

  function doFilter() {
    var selected = (filterSelect ? filterSelect.value : '').toLowerCase().trim();
    var visible = 0;
    rows.forEach(function (row) {
      var badge = row.querySelector('.badge');
      var badgeText = badge ? badge.textContent.toLowerCase() : '';
      if (!selected || selected === 'all' || badgeText.indexOf(selected) !== -1) {
        row.style.display = '';
        visible++;
      } else {
        row.style.display = 'none';
      }
    });
  }

  // Withdraw Action Handlers
  document.addEventListener('click', function (e) {
    var withdrawBtn = e.target.closest('.btn-withdraw');
    if (withdrawBtn) {
      if (confirm('Are you sure you want to withdraw this application? This action cannot be undone.')) {
        var row = withdrawBtn.closest('tr');
        if (row) {
          var badge = row.querySelector('.badge');
          if (badge) {
            badge.className = 'badge badge-rejected';
            badge.textContent = 'Withdrawn';
          }
          withdrawBtn.disabled = true;
          withdrawBtn.textContent = 'Withdrawn';
          window.showToast('Application withdrawn successfully.', 'info', 3000);
        }
      }
    }
  });

  if (filterSelect) {
    filterSelect.addEventListener('change', doFilter);
  }
  if (filterBtn) {
    filterBtn.addEventListener('click', function () {
      doFilter();
      if (window.showToast) window.showToast('Status filter applied.', 'info', 2000);
    });
  }
  if (resetBtn) {
    resetBtn.addEventListener('click', function () {
      if (filterSelect) filterSelect.value = 'all';
      doFilter();
      if (window.showToast) window.showToast('Application filter reset.', 'info', 2000);
    });
  }
}

/* -------------------------------------------------------------
   8. Student Profile Actions (profile.html)
------------------------------------------------------------- */
function initStudentProfileActions() {
  var saveProfileBtn = document.getElementById('saveProfileBtn');
  var uploadResumeBtn = document.getElementById('uploadResumeBtn');
  var resumeInput = document.getElementById('resumeFileInput');
  var openProjectBtn = document.getElementById('openAddProjectModalBtn');
  var projectModal = document.getElementById('addProjectModal');
  var closeProjectBtn = document.getElementById('closeAddProjectModalBtn');
  var cancelProjectBtn = document.getElementById('cancelAddProjectModalBtn');
  var addProjectForm = document.getElementById('addProjectForm');
  var projectTitleInput = document.getElementById('projectTitleInput');
  var projectsGrid = document.getElementById('academicProjectsGrid');

  // Add Project Modal Handlers
  if (openProjectBtn && projectModal) {
    function openModal() {
      projectModal.classList.add('active');
      if (projectTitleInput) {
        setTimeout(function () { projectTitleInput.focus(); }, 100);
      }
    }

    function closeModal() {
      projectModal.classList.remove('active');
      if (addProjectForm) addProjectForm.reset();
    }

    openProjectBtn.addEventListener('click', openModal);
    if (closeProjectBtn) closeProjectBtn.addEventListener('click', closeModal);
    if (cancelProjectBtn) cancelProjectBtn.addEventListener('click', closeModal);

    projectModal.addEventListener('click', function (e) {
      if (e.target === projectModal) closeModal();
    });

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && projectModal.classList.contains('active')) {
        closeModal();
      }
    });

    if (addProjectForm) {
      addProjectForm.addEventListener('submit', function (e) {
        var title = (projectTitleInput ? projectTitleInput.value : '').trim();
        if (!title) {
          e.preventDefault();
          if (window.showToast) window.showToast('Please provide a project title.', 'error', 3000);
          return;
        }

        // If fetch is available, perform async save with instant DOM update for seamless experience
        if (window.fetch && projectsGrid) {
          e.preventDefault();
          var formData = new FormData(addProjectForm);
          var desc = formData.get('description') || 'Academic portfolio project showcasing core engineering competencies.';
          var tech = formData.get('technologies') || 'General Engineering';
          var repo = formData.get('repoUrl') || '#';

          fetch('/student/projects/add', {
            method: 'POST',
            body: new URLSearchParams(formData)
          }).then(function () {
            // Append new project card to the DOM
            var newCard = document.createElement('div');
            newCard.className = 'card project-item-card';
            newCard.style.backgroundColor = '#fafbfc';
            newCard.style.border = '1px solid var(--color-border)';
            newCard.innerHTML = [
              '<div class="card-body">',
              '  <div class="flex-between" style="align-items: flex-start; margin-bottom: 4px;">',
              '    <h4 style="margin-bottom: 0; font-weight: 600;">' + escapeHtml(title) + '</h4>',
              '  </div>',
              '  <p style="font-size: var(--font-size-sm); color: var(--color-text-secondary); margin-bottom: var(--space-2);">' + escapeHtml(desc) + '</p>',
              '  <p style="font-size: var(--font-size-xs); color: var(--color-text-muted); margin-bottom: var(--space-3);">',
              '    <strong>Technologies:</strong> <span>' + escapeHtml(tech) + '</span>',
              '  </p>',
              '  <a href="' + escapeHtml(repo) + '" target="_blank" class="btn btn-outline btn-sm">View Project Repository</a>',
              '</div>'
            ].join('');

            projectsGrid.appendChild(newCard);
            closeModal();
            if (window.showToast) {
              window.showToast('Academic project "' + title + '" added to portfolio successfully!', 'success', 3500);
            }
          }).catch(function () {
            // Fallback: standard submit if fetch fails
            addProjectForm.submit();
          });
        }
      });
    }
  }

  // Upload Resume Handlers
  var resumeUploadForm = document.getElementById('resumeUploadForm');
  if (uploadResumeBtn && resumeInput) {
    uploadResumeBtn.addEventListener('click', function () {
      resumeInput.click();
    });

    resumeInput.addEventListener('change', function () {
      if (resumeInput.files && resumeInput.files[0]) {
        var fileName = resumeInput.files[0].name;
        if (window.showToast) {
          window.showToast('Uploading "' + fileName + '" to candidate profile...', 'info', 2000);
        }
        if (resumeUploadForm) {
          resumeUploadForm.submit();
        }
      }
    });
  }

  // Add Certification Modal Handlers
  var openCertBtn = document.getElementById('openAddCertModalBtn');
  var certModal = document.getElementById('addCertificationModal');
  var closeCertBtn = document.getElementById('closeAddCertModalBtn');
  var cancelCertBtn = document.getElementById('cancelAddCertModalBtn');
  var addCertForm = document.getElementById('addCertificationForm');
  var certTitleInput = document.getElementById('certTitleInput');
  var saveCertBtn = document.getElementById('saveCertBtn');

  if (certModal) {
    function openCertModal() {
      certModal.classList.add('active');
      if (certTitleInput) {
        setTimeout(function () {
          certTitleInput.focus();
        }, 100);
      }
    }

    function closeCertModal() {
      certModal.classList.remove('active');
      if (addCertForm) addCertForm.reset();
      if (saveCertBtn) {
        saveCertBtn.disabled = false;
        saveCertBtn.textContent = 'Save Certification';
      }
    }

    if (openCertBtn) {
      openCertBtn.addEventListener('click', openCertModal);
    }

    var emptyCertBtn = document.getElementById('openAddCertEmptyBtn');
    if (emptyCertBtn) {
      emptyCertBtn.addEventListener('click', openCertModal);
    }

    var allOpenCertBtns = document.querySelectorAll('.open-cert-modal-btn');
    if (allOpenCertBtns && allOpenCertBtns.length > 0) {
      for (var ci = 0; ci < allOpenCertBtns.length; ci++) {
        allOpenCertBtns[ci].addEventListener('click', openCertModal);
      }
    }

    if (closeCertBtn) closeCertBtn.addEventListener('click', closeCertModal);
    if (cancelCertBtn) cancelCertBtn.addEventListener('click', closeCertModal);

    certModal.addEventListener('click', function (e) {
      if (e.target === certModal) closeCertModal();
    });

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape' && certModal.classList.contains('active')) {
        closeCertModal();
      }
    });

    if (addCertForm) {
      addCertForm.addEventListener('submit', function (e) {
        var title = (certTitleInput ? certTitleInput.value : '').trim();
        if (!title) {
          e.preventDefault();
          if (window.showToast) window.showToast('Please provide a certification title.', 'error', 3000);
          return;
        }

        if (saveCertBtn) {
          saveCertBtn.disabled = true;
          saveCertBtn.textContent = 'Saving...';
        }
        if (window.showToast) {
          window.showToast('Saving accreditation credential...', 'info', 2500);
        }
      });
    }
  }
}

/* -------------------------------------------------------------
   9. Student Feedback Form (feedback.html)
------------------------------------------------------------- */
function initStudentFeedbackForm() {
  var form = document.getElementById('studentFeedbackForm');
  var timeline = document.getElementById('feedbackTimelineList');

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var companyInput = document.getElementById('feedbackCompany');
      var typeInput = document.getElementById('feedbackType');
      var commentInput = document.getElementById('feedbackComments');

      var company = companyInput ? companyInput.value : 'General Placement Drive';
      var type = typeInput ? typeInput.value : 'Technical Round';
      var comments = commentInput ? commentInput.value : 'Detailed feedback submitted.';

      if (timeline) {
        var newCard = document.createElement('div');
        newCard.className = 'card';
        newCard.style.marginBottom = 'var(--space-4)';
        newCard.innerHTML = [
          '<div class="card-header">',
          '  <div>',
          '    <h4 style="margin-bottom: 2px;">' + escapeHtml(company) + '</h4>',
          '    <div style="font-size: var(--font-size-xs); color: var(--color-text-muted);">' + escapeHtml(type) + ' &bull; Submitted Just Now</div>',
          '  </div>',
          '  <span class="badge badge-selected">New Feedback</span>',
          '</div>',
          '<div class="card-body">',
          '  <p style="font-size: var(--font-size-sm); color: var(--color-text-secondary);">' + escapeHtml(comments) + '</p>',
          '</div>'
        ].join('');
        timeline.insertBefore(newCard, timeline.firstChild);
      }

      form.reset();
      window.showToast('Feedback submitted successfully to Training & Placement Cell.', 'success', 3500);
    });
  }
}

/* -------------------------------------------------------------
   10. Recruiter Applicants Board & Schedule Flow (applicants.html)
------------------------------------------------------------- */
function initRecruiterApplicantsInteractivity() {
  var deptSelect = document.getElementById('applicantDeptFilter');
  var statusSelect = document.getElementById('applicantStatusFilter');
  var searchInput = document.getElementById('applicantSearchInput');
  var filterBtn = document.getElementById('applyApplicantFilterBtn');
  var resetBtn = document.getElementById('resetApplicantFiltersBtn');
  var emptyResetBtn = document.getElementById('emptyStateResetBtn');
  var applicantRows = document.querySelectorAll('.applicant-row');
  var deptButtons = document.querySelectorAll('[data-filter-dept]');
  var countBadge = document.getElementById('applicantCountBadge');
  var emptyRow = document.getElementById('noApplicantMatchRow');

  if (!deptSelect && !statusSelect && !searchInput && applicantRows.length === 0) return;

  function filterApplicants() {
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var statusVal = (statusSelect ? statusSelect.value : '').toUpperCase().trim();
    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();
    var normStatusVal = normalizeStatus(statusVal);

    var visibleCount = 0;

    applicantRows.forEach(function (row) {
      var rowDept = row.getAttribute('data-dept') || row.getAttribute('data-applicant-dept') || '';
      var rowRoll = row.getAttribute('data-roll') || '';
      var rowStatus = (row.getAttribute('data-status') || '').toUpperCase();
      var normRowStatus = normalizeStatus(rowStatus);
      var text = row.textContent.toLowerCase();

      var matchDept = matchDeptHelper(rowDept, deptVal, rowRoll);

      var matchStatus = true;
      if (statusVal && statusVal !== 'ALL') {
        matchStatus = (normRowStatus === normStatusVal) ||
                      (normRowStatus.indexOf(normStatusVal) !== -1) ||
                      (text.indexOf(normStatusVal.toLowerCase()) !== -1);
      }

      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      if (matchDept && matchStatus && matchSearch) {
        row.style.display = '';
        visibleCount++;
      } else {
        row.style.display = 'none';
      }
    });

    if (countBadge) {
      countBadge.textContent = 'Showing ' + visibleCount + ' of ' + applicantRows.length + ' Candidates';
    }

    if (emptyRow) {
      emptyRow.style.display = (visibleCount === 0 && applicantRows.length > 0) ? '' : 'none';
    }
  }

  function resetAll() {
    if (deptSelect) deptSelect.value = '';
    if (statusSelect) statusSelect.value = '';
    if (searchInput) searchInput.value = '';
    deptButtons.forEach(function (b) { b.classList.remove('btn-primary'); b.classList.add('btn-outline'); });
    filterApplicants();
    if (window.showToast) window.showToast('Candidate filters reset.', 'info', 2000);
  }

  if (deptSelect) deptSelect.addEventListener('change', filterApplicants);
  if (statusSelect) statusSelect.addEventListener('change', filterApplicants);
  if (searchInput) searchInput.addEventListener('input', filterApplicants);
  if (filterBtn) {
    filterBtn.addEventListener('click', function () {
      filterApplicants();
      var visible = document.querySelectorAll('.applicant-row:not([style*="display: none"])').length;
      if (window.showToast) {
        window.showToast('Filter updated: ' + visible + ' candidate(s) match.', 'info', 2000);
      }
    });
  }
  if (resetBtn) resetBtn.addEventListener('click', resetAll);
  if (emptyResetBtn) emptyResetBtn.addEventListener('click', resetAll);

  // Department filter pills in Recruiter Screening Board
  if (deptButtons.length > 0 && applicantRows.length > 0) {
    deptButtons.forEach(function (btn) {
      btn.addEventListener('click', function () {
        deptButtons.forEach(function (b) { b.classList.remove('btn-primary'); b.classList.add('btn-outline'); });
        btn.classList.add('btn-primary');
        btn.classList.remove('btn-outline');

        var targetDept = btn.getAttribute('data-filter-dept');
        if (deptSelect) {
          deptSelect.value = targetDept === 'ALL' ? '' : targetDept;
        }
        filterApplicants();
      });
    });
  }

  // Trigger initial filter evaluation on page load
  if (deptSelect || statusSelect || searchInput) {
    filterApplicants();
  }

  // Candidate Actions: Shortlist, Interview, Reject
  document.addEventListener('click', function (e) {
    var actionBtn = e.target.closest('[data-candidate-action]');
    if (!actionBtn) return;

    var action = actionBtn.getAttribute('data-candidate-action');
    var row = actionBtn.closest('tr');
    if (!row) return;

    var badge = row.querySelector('.candidate-status-badge');
    var candidateName = row.querySelector('.candidate-name') ? row.querySelector('.candidate-name').textContent : 'Candidate';

    if (action === 'shortlist') {
      if (badge) {
        badge.className = 'badge badge-shortlisted candidate-status-badge';
        badge.textContent = 'Shortlisted';
      }
      window.showToast(candidateName + ' shortlisted for technical interview round.', 'success', 3000);
    } else if (action === 'interview') {
      showScheduleModal(candidateName, row);
    } else if (action === 'reject') {
      if (confirm('Mark ' + candidateName + ' as Not Recommended for this drive?')) {
        if (badge) {
          badge.className = 'badge badge-rejected candidate-status-badge';
          badge.textContent = 'Rejected';
        }
        window.showToast(candidateName + ' marked as rejected.', 'info', 3000);
      }
    }
  });
}

function showScheduleModal(candidateName, targetRow) {
  var existingModal = document.getElementById('scheduleModal');
  if (existingModal) existingModal.remove();

  var modal = document.createElement('div');
  modal.id = 'scheduleModal';
  modal.className = 'portal-modal-overlay active';

  modal.innerHTML = [
    '<div class="portal-modal-container">',
    '  <div class="portal-modal-header">',
    '    <h3 class="portal-modal-title">Schedule Interview Session</h3>',
    '    <button type="button" class="portal-modal-close-btn" id="closeSchedModalBtn">&times;</button>',
    '  </div>',
    '  <div class="portal-modal-body">',
    '    <div style="margin-bottom: var(--space-4);">',
    '      <label class="form-label">Candidate</label>',
    '      <input type="text" class="form-control" value="' + escapeHtml(candidateName) + '" readonly>',
    '    </div>',
    '    <div class="form-row" style="margin-bottom: var(--space-4);">',
    '      <div class="form-col-6">',
    '        <label class="form-label">Interview Round</label>',
    '        <select class="form-control" id="schedRound">',
    '          <option value="Technical Round 1">Technical Round 1 (System Design)</option>',
    '          <option value="Technical Round 2">Technical Round 2 (Algorithms)</option>',
    '          <option value="Managerial Discussion">Managerial Discussion</option>',
    '          <option value="HR & Cultural Fit">HR &amp; Cultural Fit</option>',
    '        </select>',
    '      </div>',
    '      <div class="form-col-6">',
    '        <label class="form-label">Date</label>',
    '        <input type="date" class="form-control" id="schedDate" value="2026-09-22">',
    '      </div>',
    '    </div>',
    '    <div class="form-row" style="margin-bottom: var(--space-4);">',
    '      <div class="form-col-6">',
    '        <label class="form-label">Time Slot</label>',
    '        <input type="time" class="form-control" id="schedTime" value="10:30">',
    '      </div>',
    '      <div class="form-col-6">',
    '        <label class="form-label">Meeting Room / Link</label>',
    '        <input type="text" class="form-control" id="schedLink" value="https://meet.google.com/xyz-recruitment">',
    '      </div>',
    '    </div>',
    '  </div>',
    '  <div class="portal-modal-footer">',
    '    <button type="button" class="btn btn-secondary" id="cancelSchedModalBtn">Cancel</button>',
    '    <button type="button" class="btn btn-primary" id="confirmSchedModalBtn">Confirm &amp; Send Invite</button>',
    '  </div>',
    '</div>'
  ].join('');

  document.body.appendChild(modal);

  function closeModal() {
    modal.classList.remove('active');
  }

  document.getElementById('closeSchedModalBtn').addEventListener('click', closeModal);
  document.getElementById('cancelSchedModalBtn').addEventListener('click', closeModal);

  document.getElementById('confirmSchedModalBtn').addEventListener('click', function () {
    closeModal();
    if (targetRow) {
      var badge = targetRow.querySelector('.candidate-status-badge');
      if (badge) {
        badge.className = 'badge badge-in-process candidate-status-badge';
        badge.textContent = 'Interview Scheduled';
      }
    }
    window.showToast('Interview scheduled for ' + candidateName + '! Invitation dispatched to candidate email.', 'success', 3500);
  });
}

/* -------------------------------------------------------------
   11. Admin Actions: Verification, Approval & Reports
------------------------------------------------------------- */
function initAdminManagementActions() {
  // Company Approval in admin/companies.html
  document.addEventListener('click', function (e) {
    var approveBtn = e.target.closest('[data-admin-action="approve-company"]');
    if (approveBtn) {
      var row = approveBtn.closest('tr');
      if (row) {
        var badge = row.querySelector('.badge');
        if (badge) {
          badge.className = 'badge badge-selected';
          badge.textContent = 'Active Verified';
        }
        approveBtn.textContent = 'Deactivate';
        approveBtn.className = 'btn btn-outline btn-xs';
        approveBtn.setAttribute('data-admin-action', 'deactivate-company');
        window.showToast('Company partner verified and approved for campus hiring.', 'success', 3500);
      }
      return;
    }

    var deactBtn = e.target.closest('[data-admin-action="deactivate-company"]');
    if (deactBtn) {
      var row = deactBtn.closest('tr');
      if (row) {
        var badge = row.querySelector('.badge');
        if (badge) {
          badge.className = 'badge badge-rejected';
          badge.textContent = 'Suspended';
        }
        deactBtn.textContent = 'Re-Activate';
        deactBtn.className = 'btn btn-primary btn-xs';
        deactBtn.setAttribute('data-admin-action', 'approve-company');
        window.showToast('Company access suspended.', 'info', 3000);
      }
      return;
    }

    // Export Analytics Report
    var exportBtn = e.target.closest('#exportPlacementReportBtn');
    if (exportBtn) {
      window.showToast('Generating official Placement Accreditation CSV Report (NIRF/NAAC Compliant)...', 'info', 2500);
      setTimeout(function () {
        window.location.href = '/admin/export/accreditation-csv';
      }, 400);
    }
  });
}

/* -------------------------------------------------------------
   11. Admin Students Table Live Filter (admin/students.html)
------------------------------------------------------------- */
function initAdminStudentsFilter() {
  var form = document.getElementById('adminStudentFilterForm');
  var deptSelect = document.getElementById('adminDeptFilter');
  var statusSelect = document.getElementById('adminStatusFilter');
  var searchInput = document.getElementById('adminStudentSearch');
  var filterBtn = document.getElementById('adminStudentFilterBtn');
  var resetBtn = document.getElementById('adminStudentResetBtn');
  var emptyRow = document.getElementById('noAdminStudentMatchRow');

  if (!deptSelect && !statusSelect && !searchInput) return;

  function filterStudents(e) {
    if (e && e.preventDefault) e.preventDefault();

    var studentRows = document.querySelectorAll('.admin-student-row');
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var statusVal = (statusSelect ? statusSelect.value : '').toUpperCase().trim();
    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();

    var visible = 0;
    studentRows.forEach(function (row) {
      var rowDept = row.getAttribute('data-dept') || '';
      var rowRoll = row.getAttribute('data-roll') || (row.querySelector('td:nth-child(2)') ? row.querySelector('td:nth-child(2)').textContent.trim() : '');
      var rowStatus = (row.getAttribute('data-status') || '').toUpperCase();
      var text = row.textContent.toLowerCase();

      var matchDept = matchDeptHelper(rowDept, deptVal, rowRoll);

      var matchStatus = true;
      if (statusVal && statusVal !== 'ALL') {
        if (statusVal === 'UNPLACED') {
          matchStatus = !rowStatus || rowStatus === 'UNPLACED' || rowStatus === 'SEEKING' || text.indexOf('unplaced') !== -1 || text.indexOf('seeking') !== -1;
        } else {
          matchStatus = rowStatus === statusVal || text.indexOf(statusVal.toLowerCase()) !== -1;
        }
      }

      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      if (matchDept && matchStatus && matchSearch) {
        row.style.display = '';
        visible++;
      } else {
        row.style.display = 'none';
      }
    });

    if (emptyRow) {
      emptyRow.style.display = (visible === 0 && studentRows.length > 0) ? '' : 'none';
    }

    if (window.history && window.history.replaceState) {
      var params = new URLSearchParams();
      if (deptVal && deptVal !== 'ALL') params.set('department', deptSelect.value);
      if (statusVal) params.set('status', statusSelect.value);
      if (searchVal) params.set('query', searchInput.value);
      var newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
      window.history.replaceState(null, '', newUrl);
    }

    return visible;
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var count = filterStudents(e);
      if (window.showToast) window.showToast('Showing ' + count + ' student(s).', 'info', 2000);
    });
  }

  if (deptSelect) deptSelect.addEventListener('change', function () { filterStudents(); });
  if (statusSelect) statusSelect.addEventListener('change', function () { filterStudents(); });
  if (searchInput) searchInput.addEventListener('input', function () { filterStudents(); });
  if (filterBtn) {
    filterBtn.addEventListener('click', function (e) {
      e.preventDefault();
      var count = filterStudents(e);
      if (window.showToast) window.showToast('Showing ' + count + ' student(s).', 'info', 2000);
    });
  }
  if (resetBtn) {
    resetBtn.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault();
      if (deptSelect) deptSelect.value = 'ALL';
      if (statusSelect) statusSelect.value = '';
      if (searchInput) searchInput.value = '';
      filterStudents();
      if (window.showToast) window.showToast('Student filters reset.', 'info', 2000);
    });
  }

  // Initial evaluation on load if values present
  var hasInitialFilter = (deptSelect && deptSelect.value && deptSelect.value !== 'ALL') ||
                         (statusSelect && statusSelect.value) ||
                         (searchInput && searchInput.value);
  if (hasInitialFilter) {
    filterStudents();
  }
}

/* -------------------------------------------------------------
   12. Admin Applications Live Filter (admin/applications.html)
------------------------------------------------------------- */
function initAdminApplicationsFilter() {
  var form = document.getElementById('adminAppFilterForm');
  var deptSelect = document.getElementById('adminAppDeptFilter');
  var companySelect = document.getElementById('adminAppCompanyFilter');
  var statusSelect = document.getElementById('adminAppStatusFilter');
  var searchInput = document.getElementById('adminAppSearch');
  var filterBtn = document.getElementById('adminAppFilterBtn');
  var resetBtn = document.getElementById('adminAppResetBtn');
  var emptyRow = document.getElementById('noAdminAppMatchRow');

  if (!deptSelect && !companySelect && !statusSelect && !searchInput) return;

  function filterApplications(e) {
    if (e && e.preventDefault) e.preventDefault();

    var appRows = document.querySelectorAll('.admin-app-row');
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var compVal = (companySelect ? companySelect.value : '').toLowerCase().trim();
    var statusVal = (statusSelect ? statusSelect.value : '').toUpperCase().trim();
    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();

    var visible = 0;
    appRows.forEach(function (row) {
      var rowDept = row.getAttribute('data-dept') || '';
      var rowComp = (row.getAttribute('data-company') || '').toLowerCase();
      var rowStatus = (row.getAttribute('data-status') || '').toUpperCase();
      var text = row.textContent.toLowerCase();

      var matchDept = matchDeptHelper(rowDept, deptVal);
      var matchComp = !compVal || rowComp.indexOf(compVal) !== -1 || text.indexOf(compVal) !== -1;
      var matchStatus = !statusVal || statusVal === 'ALL' || rowStatus === statusVal || text.indexOf(statusVal.toLowerCase()) !== -1;
      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      if (matchDept && matchComp && matchStatus && matchSearch) {
        row.style.display = '';
        visible++;
      } else {
        row.style.display = 'none';
      }
    });

    if (emptyRow) {
      emptyRow.style.display = (visible === 0 && appRows.length > 0) ? '' : 'none';
    }

    if (window.history && window.history.replaceState) {
      var params = new URLSearchParams();
      if (deptVal) params.set('department', deptSelect.value);
      if (compVal) params.set('company', companySelect.value);
      if (statusVal) params.set('status', statusSelect.value);
      if (searchVal) params.set('query', searchInput.value);
      var newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
      window.history.replaceState(null, '', newUrl);
    }

    return visible;
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var count = filterApplications(e);
      if (window.showToast) window.showToast('Filter applied: ' + count + ' application(s) matching.', 'info', 2000);
    });
  }

  if (deptSelect) deptSelect.addEventListener('change', function () { filterApplications(); });
  if (companySelect) companySelect.addEventListener('change', function () { filterApplications(); });
  if (statusSelect) statusSelect.addEventListener('change', function () { filterApplications(); });
  if (searchInput) searchInput.addEventListener('input', function () { filterApplications(); });

  if (filterBtn) {
    filterBtn.addEventListener('click', function (e) {
      e.preventDefault();
      var count = filterApplications(e);
      if (window.showToast) window.showToast('Filter applied: ' + count + ' application(s) matching.', 'info', 2000);
    });
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault();
      if (deptSelect) deptSelect.value = '';
      if (companySelect) companySelect.value = '';
      if (statusSelect) statusSelect.value = '';
      if (searchInput) searchInput.value = '';
      filterApplications();
      if (window.showToast) window.showToast('Application filters reset.', 'info', 2000);
    });
  }

  // Initial evaluation on load if values present
  var hasInitialFilter = (deptSelect && deptSelect.value && deptSelect.value !== 'ALL') ||
                         (companySelect && companySelect.value) ||
                         (statusSelect && statusSelect.value) ||
                         (searchInput && searchInput.value);
  if (hasInitialFilter) {
    filterApplications();
  }
}

/* -------------------------------------------------------------
   13. Admin Jobs Live Filter (admin/jobs.html)
------------------------------------------------------------- */
function initAdminJobsFilter() {
  var form = document.getElementById('adminJobFilterForm');
  var statusSelect = document.getElementById('adminJobStatusFilter');
  var deptSelect = document.getElementById('adminJobDeptFilter');
  var searchInput = document.getElementById('adminJobSearch');
  var filterBtn = document.getElementById('adminJobFilterBtn');
  var resetBtn = document.getElementById('adminJobResetBtn');
  var emptyRow = document.getElementById('noAdminJobMatchRow');

  if (!statusSelect && !deptSelect && !searchInput) return;

  function filterJobs(e) {
    if (e && e.preventDefault) e.preventDefault();

    var jobRows = document.querySelectorAll('.admin-job-row');
    var statusVal = (statusSelect ? statusSelect.value : '').toUpperCase().trim();
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();

    var visible = 0;
    jobRows.forEach(function (row) {
      var rowStatus = (row.getAttribute('data-status') || '').toUpperCase();
      var rowDept = row.getAttribute('data-dept') || '';
      var text = row.textContent.toLowerCase();

      var matchStatus = !statusVal || statusVal === 'ALL' || rowStatus === statusVal || text.indexOf(statusVal.toLowerCase()) !== -1;
      var matchDept = matchDeptHelper(rowDept, deptVal);
      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      if (matchStatus && matchDept && matchSearch) {
        row.style.display = '';
        visible++;
      } else {
        row.style.display = 'none';
      }
    });

    if (emptyRow) {
      emptyRow.style.display = (visible === 0 && jobRows.length > 0) ? '' : 'none';
    }

    if (window.history && window.history.replaceState) {
      var params = new URLSearchParams();
      if (statusVal) params.set('status', statusSelect.value);
      if (deptVal) params.set('department', deptSelect.value);
      if (searchVal) params.set('query', searchInput.value);
      var newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
      window.history.replaceState(null, '', newUrl);
    }

    return visible;
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var count = filterJobs(e);
      if (window.showToast) window.showToast('Showing ' + count + ' recruitment drive(s).', 'info', 2000);
    });
  }

  if (statusSelect) statusSelect.addEventListener('change', function () { filterJobs(); });
  if (deptSelect) deptSelect.addEventListener('change', function () { filterJobs(); });
  if (searchInput) searchInput.addEventListener('input', function () { filterJobs(); });

  if (filterBtn) {
    filterBtn.addEventListener('click', function (e) {
      e.preventDefault();
      var count = filterJobs(e);
      if (window.showToast) window.showToast('Showing ' + count + ' recruitment drive(s).', 'info', 2000);
    });
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault();
      if (statusSelect) statusSelect.value = '';
      if (deptSelect) deptSelect.value = '';
      if (searchInput) searchInput.value = '';
      filterJobs();
      if (window.showToast) window.showToast('Job filters reset.', 'info', 2000);
    });
  }

  var hasInitialFilter = (statusSelect && statusSelect.value) ||
                         (deptSelect && deptSelect.value) ||
                         (searchInput && searchInput.value);
  if (hasInitialFilter) {
    filterJobs();
  }
}

/* -------------------------------------------------------------
   14. Admin Feedback Live Filter (admin/feedback.html)
------------------------------------------------------------- */
function initAdminFeedbackFilter() {
  var form = document.getElementById('adminFeedbackFilterForm');
  var deptSelect = document.getElementById('adminFeedbackDeptFilter');
  var companySelect = document.getElementById('adminFeedbackCompanyFilter');
  var ratingSelect = document.getElementById('adminFeedbackRatingFilter');
  var searchInput = document.getElementById('adminFeedbackSearch');
  var filterBtn = document.getElementById('adminFeedbackFilterBtn');
  var resetBtn = document.getElementById('adminFeedbackResetBtn');
  var emptyRow = document.getElementById('noAdminFeedbackMatchRow');

  if (!deptSelect && !companySelect && !ratingSelect && !searchInput) return;

  function filterFeedback(e) {
    if (e && e.preventDefault) e.preventDefault();

    var feedbackRows = document.querySelectorAll('.admin-feedback-row');
    var deptVal = (deptSelect ? deptSelect.value : '').toUpperCase().trim();
    var compVal = (companySelect ? companySelect.value : '').toLowerCase().trim();
    var ratingVal = (ratingSelect ? ratingSelect.value : '').trim();
    var searchVal = (searchInput ? searchInput.value : '').toLowerCase().trim();

    var visible = 0;
    feedbackRows.forEach(function (row) {
      var rowDept = row.getAttribute('data-dept') || (row.querySelector('td:nth-child(2)') ? row.querySelector('td:nth-child(2)').textContent.trim() : '');
      var rowRoll = row.getAttribute('data-roll') || '';
      var rowComp = (row.getAttribute('data-company') || '').toLowerCase();
      var rowRating = (row.getAttribute('data-rating') || '').trim();
      var text = row.textContent.toLowerCase();

      var matchDept = matchDeptHelper(rowDept, deptVal, rowRoll);
      var matchComp = !compVal || rowComp.indexOf(compVal) !== -1 || text.indexOf(compVal) !== -1;
      var matchRating = !ratingVal || rowRating === ratingVal || text.indexOf(ratingVal + ' / 5') !== -1;
      var matchSearch = !searchVal || text.indexOf(searchVal) !== -1;

      if (matchDept && matchComp && matchRating && matchSearch) {
        row.style.display = '';
        visible++;
      } else {
        row.style.display = 'none';
      }
    });

    if (emptyRow) {
      emptyRow.style.display = (visible === 0 && feedbackRows.length > 0) ? '' : 'none';
    }

    if (window.history && window.history.replaceState) {
      var params = new URLSearchParams();
      if (deptVal) params.set('department', deptSelect.value);
      if (compVal) params.set('company', companySelect.value);
      if (ratingVal) params.set('rating', ratingSelect.value);
      if (searchVal) params.set('query', searchInput.value);
      var newUrl = window.location.pathname + (params.toString() ? '?' + params.toString() : '');
      window.history.replaceState(null, '', newUrl);
    }

    return visible;
  }

  if (form) {
    form.addEventListener('submit', function (e) {
      e.preventDefault();
      var count = filterFeedback(e);
      if (window.showToast) window.showToast('Showing ' + count + ' feedback record(s).', 'info', 2000);
    });
  }

  if (deptSelect) deptSelect.addEventListener('change', function () { filterFeedback(); });
  if (companySelect) companySelect.addEventListener('change', function () { filterFeedback(); });
  if (ratingSelect) ratingSelect.addEventListener('change', function () { filterFeedback(); });
  if (searchInput) searchInput.addEventListener('input', function () { filterFeedback(); });

  if (filterBtn) {
    filterBtn.addEventListener('click', function (e) {
      e.preventDefault();
      var count = filterFeedback(e);
      if (window.showToast) window.showToast('Showing ' + count + ' feedback record(s).', 'info', 2000);
    });
  }

  if (resetBtn) {
    resetBtn.addEventListener('click', function (e) {
      if (e && e.preventDefault) e.preventDefault();
      if (deptSelect) deptSelect.value = '';
      if (companySelect) companySelect.value = '';
      if (ratingSelect) ratingSelect.value = '';
      if (searchInput) searchInput.value = '';
      filterFeedback();
      if (window.showToast) window.showToast('Feedback filters reset.', 'info', 2000);
    });
  }

  var hasInitialFilter = (deptSelect && deptSelect.value && deptSelect.value !== 'ALL') ||
                         (companySelect && companySelect.value) ||
                         (ratingSelect && ratingSelect.value) ||
                         (searchInput && searchInput.value);
  if (hasInitialFilter) {
    filterFeedback();
  }
}

/* -------------------------------------------------------------
   12. URL Parameter Alerts & Redirect Banners
------------------------------------------------------------- */
function initUrlParamNotifications() {
  var search = window.location.search;
  if (!search) return;

  var params = new URLSearchParams(search);

  if (params.get('applied') === 'true') {
    window.showToast('Application successfully recorded in placement pipeline.', 'success', 4000);
  } else if (params.get('created') === 'true') {
    window.showToast('New campus recruitment drive published successfully.', 'success', 4000);
  } else if (params.get('registered') === 'true') {
    window.showToast('Account registered successfully. Please sign in with your credentials.', 'success', 4000);
  } else if (params.get('logout') === 'true') {
    window.showToast('You have been securely signed out.', 'info', 3000);
  } else if (params.get('projectAdded') === 'true') {
    window.showToast('Academic project added to your portfolio successfully!', 'success', 4000);
  } else if (params.get('projectDeleted') === 'true') {
    window.showToast('Project removed from your academic portfolio.', 'info', 3000);
  } else if (params.get('updated') === 'true') {
    window.showToast('Profile information successfully saved to placement database.', 'success', 3500);
  }
}

/* -------------------------------------------------------------
   Helper: Escape HTML
------------------------------------------------------------- */
function escapeHtml(str) {
  var div = document.createElement('div');
  div.appendChild(document.createTextNode(str));
  return div.innerHTML;
}

/* -------------------------------------------------------------
   Helper: Canonical Department Classifier & Matcher
   Treats short forms (CSE, IT, ECE, EEE/EE, MECH, CIVIL) and full
   forms as identical, while strictly preventing cross-department leaks.
------------------------------------------------------------- */
function getCanonicalDeptCodes(str) {
  if (!str) return [];
  var s = (' ' + str + ' ').toUpperCase().replace(/&/g, ' AND ');
  var codes = [];

  // 1. CSE: Computer Science & Engineering
  if (s.indexOf('COMPUTER SCIENCE') !== -1 ||
      s.indexOf('COMPUTER ENG') !== -1 ||
      s.indexOf('COMP SCI') !== -1 ||
      /\bCSE\b/.test(s) ||
      /\bCS\b/.test(s) ||
      /CSE\d/.test(s)) {
    codes.push('CSE');
  }

  // 2. IT: Information Technology
  if (s.indexOf('INFORMATION TECH') !== -1 ||
      s.indexOf('INFO TECH') !== -1 ||
      /\bIT\b/.test(s) ||
      /IT\d/.test(s)) {
    codes.push('IT');
  }

  // 3. EEE / EE: Electrical Engineering (including Electrical & Electronics Engineering)
  var isElectrical = s.indexOf('ELECTRICAL') !== -1 ||
                     /\bEEE\b/.test(s) ||
                     /\bEE\b/.test(s) ||
                     /EEE\d/.test(s) ||
                     /EE\d/.test(s);
  if (isElectrical) {
    codes.push('EEE');
  }

  // 4. ECE: Electronics and Communication Engineering
  if (s.indexOf('COMMUNICATION') !== -1 ||
      /\bECE\b/.test(s) ||
      /ECE\d/.test(s) ||
      (s.indexOf('ELECTRONIC') !== -1 && !isElectrical)) {
    codes.push('ECE');
  }

  // 5. MECH: Mechanical Engineering
  if (s.indexOf('MECHANICAL') !== -1 ||
      /\bMECH\b/.test(s) ||
      /\bME\b/.test(s) ||
      /ME\d/.test(s) ||
      /MECH\d/.test(s)) {
    codes.push('MECH');
  }

  // 6. CIVIL: Civil Engineering
  if (s.indexOf('CIVIL') !== -1 ||
      /\bCIVIL\b/.test(s) ||
      /CIVIL\d/.test(s) ||
      (/\bCE\b/.test(s) && s.indexOf('COMPUTER') === -1)) {
    codes.push('CIVIL');
  }

  return codes;
}

function matchDeptHelper(rowDeptText, selectedDept, rowRoll) {
  if (!selectedDept || selectedDept === 'ALL' || selectedDept === '') return true;
  var target = (selectedDept || '').toUpperCase().trim();
  if (target === 'ALL' || target === 'ANY' || target.indexOf('ALL DEPARTMENTS') !== -1) return true;

  var targetCodes = getCanonicalDeptCodes(selectedDept);
  var rowCombined = (rowDeptText || '') + (rowRoll ? (' ' + rowRoll) : '');
  var rowCodes = getCanonicalDeptCodes(rowCombined);

  // If both have recognized canonical codes, strictly compare canonical codes
  if (targetCodes.length > 0 && rowCodes.length > 0) {
    for (var i = 0; i < targetCodes.length; i++) {
      if (rowCodes.indexOf(targetCodes[i]) !== -1) return true;
    }
    return false; // Distinct known departments -> NO MATCH
  }

  // Fallback for custom/unrecognized departments only (avoid generic stop words like "ENGINEERING")
  var d = (rowDeptText || '').toUpperCase().trim();
  if (d === target) return true;
  var tokens = target.split(/[,;|/\s]+/);
  for (var j = 0; j < tokens.length; j++) {
    var tok = tokens[j].trim();
    if (tok.length > 3 && tok !== 'ENGINEERING' && tok !== 'DEPARTMENT' && tok !== 'STUDENT' && tok !== 'DISCIPLINES' && tok !== 'OTHER' && tok !== 'TECHNOLOGY') {
      if (d.indexOf(tok) !== -1 || tok.indexOf(d) !== -1) return true;
    }
  }

  return false;
}

/* -------------------------------------------------------------
   Helper: Normalize Status String
------------------------------------------------------------- */
function normalizeStatus(str) {
  return (str || '').replace(/[_\s-]+/g, ' ').toUpperCase().trim();
}

