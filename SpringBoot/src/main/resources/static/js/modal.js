// profile Modal
const profile_modals = document.querySelectorAll(".upload-picture");
const profile_openButtons = document.querySelectorAll(".profile-open");
const profile_closeButtons = document.querySelectorAll(".modal-close");

function closeModal_profile() {
  profile_modals.forEach((profile_modal) => {
    profile_modal.setAttribute("aria-hidden", "true");
  });
}
function openModal_profile() {
  profile_modals.forEach((profile_modal) => {
    profile_modal.setAttribute("aria-hidden", "false");
  });
}

profile_openButtons.forEach((btn) =>
  btn.addEventListener("click", openModal_profile)
);
profile_closeButtons.forEach((btn) =>
  btn.addEventListener("click", closeModal_profile)
);
// Delete Modal
const delete_modals = document.querySelectorAll(".delete-employee");
const delete_openButtons = document.querySelectorAll(".delete-open");
const delete_closeButtons = document.querySelectorAll(".modal-close");

function closeModal_delete() {
  delete_modals.forEach((delete_modal) => {
    delete_modal.setAttribute("aria-hidden", "true");
  });
}
function openModal_delete() {
  delete_modals.forEach((delete_modal) => {
    delete_modal.setAttribute("aria-hidden", "false");
  });
}

delete_openButtons.forEach((btn) =>
  btn.addEventListener("click", openModal_delete)
);
delete_closeButtons.forEach((btn) =>
  btn.addEventListener("click", closeModal_delete)
);
// add Modal
const add_modals = document.querySelectorAll(".add-employee");
const add_openButtons = document.querySelectorAll(".add-open");
const add_closeButtons = document.querySelectorAll(".modal-close");

function closeModal_add() {
  add_modals.forEach((add_modal) => {
    add_modal.setAttribute("aria-hidden", "true");
  });
}
function openModal_add() {
  add_modals.forEach((add_modal) => {
    add_modal.setAttribute("aria-hidden", "false");
  });
}

add_openButtons.forEach((btn) => btn.addEventListener("click", openModal_add));
add_closeButtons.forEach((btn) =>
  btn.addEventListener("click", closeModal_add)
);

// View Modal
const view_modals = document.querySelectorAll(".view-employee");
const view_openButtons = document.querySelectorAll(".view-open");
const view_closeButtons = document.querySelectorAll(".modal-close");

function closeModal() {
  view_modals.forEach((view_modal) => {
    view_modal.setAttribute("aria-hidden", "true");
  });
}
function openModal() {
  view_modals.forEach((view_modal) => {
    view_modal.setAttribute("aria-hidden", "false");
  });
}

view_openButtons.forEach((btn) => btn.addEventListener("click", openModal));
view_closeButtons.forEach((btn) => btn.addEventListener("click", closeModal));
