// =========================================
// NAVBAR
// =========================================

const navbar =
  document.querySelector(
    '.navbar'
  );


function actualizarNavbar() {

  if (
    window.scrollY > 40
  ) {

    navbar.classList.add(
      'scrolled'
    );

  } else {

    navbar.classList.remove(
      'scrolled'
    );

  }

}


window.addEventListener(
  'scroll',
  actualizarNavbar,
  {
    passive: true
  }
);


actualizarNavbar();


// =========================================
// ANIMACIÓN AL HACER SCROLL
// =========================================

const elementosReveal =
  document.querySelectorAll(
    '.reveal'
  );


const observer =
  new IntersectionObserver(
    (entries) => {

      entries.forEach(
        (entry) => {

          if (
            entry.isIntersecting
          ) {

            entry.target
              .classList
              .add(
                'visible'
              );

            observer.unobserve(
              entry.target
            );

          }

        }
      );

    },
    {
      threshold: 0.10
    }
  );


elementosReveal.forEach(
  (elemento) => {

    observer.observe(
      elemento
    );

  }
);


// =========================================
// SCROLL SUAVE
// =========================================

const enlacesInternos =
  document.querySelectorAll(
    'a[href^="#"]'
  );


enlacesInternos.forEach(
  (enlace) => {

    enlace.addEventListener(
      'click',
      (event) => {

        const destinoId =
          enlace.getAttribute(
            'href'
          );

        if (
          !destinoId ||
          destinoId === '#'
        ) {

          return;

        }

        const destino =
          document.querySelector(
            destinoId
          );

        if (
          !destino
        ) {

          return;

        }

        event.preventDefault();

        destino.scrollIntoView(
          {
            behavior:
              'smooth',

            block:
              'start'
          }
        );

      }
    );

  }
);


// =========================================
// LIGHTBOX / GALERÍA
// =========================================

const lightbox =
  document.getElementById(
    'lightbox'
  );


const lightboxImage =
  document.getElementById(
    'lightboxImage'
  );


const lightboxTitle =
  document.getElementById(
    'lightboxTitle'
  );


const lightboxClose =
  document.getElementById(
    'lightboxClose'
  );


const galleryTriggers =
  document.querySelectorAll(
    '.gallery-trigger'
  );


function abrirLightbox(
  imagen,
  titulo
) {

  lightboxImage.src =
    imagen;

  lightboxImage.alt =
    titulo;

  lightboxTitle.textContent =
    titulo;

  lightbox.classList.add(
    'active'
  );

  lightbox.setAttribute(
    'aria-hidden',
    'false'
  );

  document.body
    .classList
    .add(
      'lightbox-open'
    );

}


function cerrarLightbox() {

  lightbox.classList.remove(
    'active'
  );

  lightbox.setAttribute(
    'aria-hidden',
    'true'
  );

  document.body
    .classList
    .remove(
      'lightbox-open'
    );

  setTimeout(
    () => {

      lightboxImage.src =
        '';

    },
    250
  );

}


galleryTriggers.forEach(
  (trigger) => {

    trigger.addEventListener(
      'click',
      () => {

        const imagen =
          trigger.dataset.image;

        const titulo =
          trigger.dataset.title;

        abrirLightbox(
          imagen,
          titulo
        );

      }
    );

  }
);


lightboxClose.addEventListener(
  'click',
  cerrarLightbox
);


lightbox.addEventListener(
  'click',
  (event) => {

    if (
      event.target === lightbox
    ) {

      cerrarLightbox();

    }

  }
);


document.addEventListener(
  'keydown',
  (event) => {

    if (
      event.key === 'Escape' &&
      lightbox.classList.contains(
        'active'
      )
    ) {

      cerrarLightbox();

    }

  }
);