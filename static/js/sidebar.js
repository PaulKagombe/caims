document.addEventListener("DOMContentLoaded", function () {

    // Mobile sidebar toggle (off-canvas below 992px — see sidebar.css)

    const toggle = document.getElementById("sidebarToggle");

    const sidebar = document.querySelector(".sidebar");

    if(toggle && sidebar){

        toggle.addEventListener("click", function(e){

            e.stopPropagation();

            sidebar.classList.toggle("show");

        });

        // Close when clicking outside the open sidebar on mobile
        document.addEventListener("click", function(e){

            if(sidebar.classList.contains("show")
                    && !sidebar.contains(e.target)
                    && e.target !== toggle){

                sidebar.classList.remove("show");

            }

        });

    }

    // Accordion sections (MAIN, INVENTORY, PROCUREMENT, etc.)
    // Only one section's menu is open at a time. The group containing
    // the active page starts "open" server-side (see sidebar.html); we
    // just need to give its menu an initial max-height so the CSS
    // max-height transition has something to animate from/to.

    const groups = document.querySelectorAll(".sidebar-group");

    function openGroup(group){

        const menu = group.querySelector(".sidebar-menu");

        group.classList.add("open");
        menu.style.maxHeight = menu.scrollHeight + "px";

    }

    function closeGroup(group){

        const menu = group.querySelector(".sidebar-menu");

        group.classList.remove("open");
        menu.style.maxHeight = null;

    }

    groups.forEach(group => {

        if(group.classList.contains("open")){
            openGroup(group);
        }

        const toggleBtn = group.querySelector(".sidebar-section-toggle");

        toggleBtn.addEventListener("click", function(){

            const alreadyOpen = group.classList.contains("open");

            groups.forEach(closeGroup);

            if(!alreadyOpen){
                openGroup(group);
            }

        });

    });

});