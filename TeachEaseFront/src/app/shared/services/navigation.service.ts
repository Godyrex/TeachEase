import {Injectable, OnInit} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {SessionStorageService} from './user/session-storage.service';
import {UserResponse} from '../models/user/UserResponse';

export interface IMenuItem {
    id?: string;
    title?: string;
    description?: string;
    type: string;       // Possible values: link/dropDown/extLink
    name?: string;      // Used as display text for item and title for separator type
    state?: string;     // Router state
    icon?: string;      // Material icon name
    tooltip?: string;   // Tooltip text
    disabled?: boolean; // If true, item will not be appeared in sidenav.
    sub?: IChildItem[]; // Dropdown items
    badges?: IBadge[];
    active?: boolean;
    roles?: string[];
}
export interface IChildItem {
    id?: string;
    parentId?: string;
    type?: string;
    name: string;       // Display text
    state?: string;     // Router state
    icon?: string;
    sub?: IChildItem[];
    active?: boolean;
    roles?: string[];
}

interface IBadge {
    color: string;      // primary/accent/warn/hex color codes(#fff000)
    value: string;      // Display text
}

interface ISidebarState {
    sidenavOpen?: boolean;
    childnavOpen?: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class NavigationService {
    constructor(private storageService: SessionStorageService) {
        this.user$ = this.storageService.getUser();
        this.user$.subscribe(user1 => {
            this.user = user1;
            console.log('User:', this.user);
            if (this.user) {
                this.setDefaultMenu();
                console.log('User is not null:', this.user);
                this.menuItems.next(this.filterMenuItemsByUser(this.defaultMenu, this.user));
            }
        });
    }
    public sidebarState: ISidebarState = {
        sidenavOpen: false,
        childnavOpen: false
    };
    selectedItem: IMenuItem;
    user: UserResponse = this.storageService.getUserFromSession();
    user$: Observable<UserResponse | null>;
    defaultMenu: IMenuItem[] = null;

    // sets iconMenu as default;
    menuItems = new BehaviorSubject<IMenuItem[]>(this.defaultMenu);
    // navigation component has subscribed to this Observable
    menuItems$ = this.menuItems.asObservable();
    setDefaultMenu() {
        this.defaultMenu = [
            {
                name: 'Dashboard',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-Bar-Chart',
                sub: [
                    { icon: 'i-Clock-3', name: 'Version 1', state: '/dashboard/v1', type: 'link' },
                    { icon: 'i-Clock-4', name: 'Version 2', state: '/dashboard/v2', type: 'link' },
                    { icon: 'i-Over-Time', name: 'Version 3', state: '/dashboard/v3', type: 'link' },
                    { icon: 'i-Clock', name: 'Version 4', state: '/dashboard/v4', type: 'link' },
                ]
            },
            {
                name: 'UI kits',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing.',
                type: 'dropDown',
                icon: 'i-Library',
                sub: [
                    { icon: 'i-Bell', name: 'Alerts', state: '/uikits/alerts', type: 'link' },
                    { icon: 'i-Split-Horizontal-2-Window', name: 'Accordions', state: '/uikits/accordions', type: 'link' },
                    { icon: 'i-Medal-2', name: 'Badges', state: '/uikits/badges', type: 'link' },
                    {
                        icon: 'i-Arrow-Right-in-Circle',
                        name: 'Buttons',
                        type: 'dropDown',
                        sub: [
                            { name: 'Bootstrap Buttons', state: '/uikits/buttons', type: 'link' },
                            { name: 'Loding Buttons', state: '/uikits/buttons-loading', type: 'link' }
                        ]
                    },
                    { icon: 'i-ID-Card', name: 'Cards', state: '/uikits/cards', type: 'link' },
                    { icon: 'i-Line-Chart-2', name: 'Cards metrics', state: '/uikits/cards-metrics', type: 'link' },
                    { icon: 'i-Credit-Card', name: 'Cards widget', state: '/uikits/cards-widget', type: 'link' },
                    { icon: 'i-Full-Cart', name: 'Cards ecommerce', state: '/uikits/cards-ecommerce', type: 'link' },
                    { icon: 'i-Duplicate-Window', name: 'Modals', state: '/uikits/modals', type: 'link' },
                    { icon: 'i-Speach-Bubble-3', name: 'Popover', state: '/uikits/popover', type: 'link' },
                    { icon: 'i-Like', name: 'Rating', state: '/uikits/rating', type: 'link' },
                    { icon: 'i-Loading-3', name: 'Loaders', state: '/uikits/loaders', type: 'link' },
                ]
            },
            {
                name: 'Apps',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-Computer-Secure',
                sub: [
                    { icon: 'i-Add-File', name: 'Invoice Builder', state: '/invoice', type: 'link' },
                    { icon: 'i-Email', name: 'Inbox', state: '/inbox', type: 'link' },
                    { icon: 'i-Speach-Bubble-3', name: 'Chat', state: '/chat', type: 'link' },
                    { icon: 'i-Calendar', name: 'Calendar', state: '/calendar', type: 'link' },
                ]
            },
            {
                name: 'Forms',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-File-Clipboard-File--Text',
                sub: [
                    { icon: 'i-File-Clipboard-Text--Image', name: 'Basic components', state: '/forms/basic', type: 'link' },
                    { icon: 'i-Split-Vertical', name: 'Form layouts', state: '/forms/layouts', type: 'link' },
                    { icon: 'i-Receipt-4', name: 'Input Group', state: '/forms/input-group', type: 'link' },
                    { icon: 'i-File-Edit', name: 'Input Mask', state: '/forms/input-mask', type: 'link' },
                    { icon: 'i-Tag-2', name: 'Tag Input', state: '/forms/tag-input', type: 'link' },
                    { icon: 'i-Width-Window', name: 'Wizard', state: '/forms/wizard', type: 'link' },
                    { icon: 'i-Crop-2', name: 'Image Cropper', state: '/forms/img-cropper', type: 'link' },
                ]
            },
            {
                name: 'Data Tables',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-File-Horizontal-Text',
                sub: [
                    { icon: 'i-File-Horizontal-Text', name: 'List', state: '/tables/list', type: 'link' },
                    { icon: 'i-Full-View-Window', name: 'Fullscreen', state: '/tables/full', type: 'link' },
                    { icon: 'i-Code-Window', name: 'Paging', state: '/tables/paging', type: 'link' },
                    { icon: 'i-Filter-2', name: 'Filter', state: '/tables/filter', type: 'link' },
                ]
            },
            {
                name: 'Sessions',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-Administrator',
                sub: [
                    { icon: 'i-Add-User', name: 'Sign up', state: '/sessions/signup', type: 'link' },
                    { icon: 'i-Checked-User', name: 'Sign in', state: '/sessions/signin', type: 'link' },
                    { icon: 'i-Find-User', name: 'Forgot', state: '/sessions/forgot', type: 'link' }
                ]
            },
            {
                name: 'Tools',
                description: 'Tools for managing resources.',
                type: 'dropDown',
                icon: 'i-Administrator',
                sub: [
                    { icon: 'i-Add-User', name: 'Users', state: '/tools/users', type: 'link' },
                ],
                roles: ['ADMIN']
            },
            {
                name: 'Pages',
                description: 'Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
                type: 'dropDown',
                icon: 'i-Windows-2',
                sub: [
                    { icon: 'i-Male', name: 'User Profile', state: '/pages/profile', type: 'link' }
                ]
            },
        ];
    }
    filterMenuItemsByUser(menuItems: IMenuItem[], user: UserResponse): IMenuItem[] {
        console.log('Filtering menu items by user:', user);
        return menuItems.filter(item => {
            const accessibleByRole = (!item.roles || item.roles.some(role => user.role.includes(role)));
            if (accessibleByRole && item.sub) {
                item.sub = this.filterChildItemsByRole(item.sub, user);
            }
            return accessibleByRole;
        });
    }
    filterChildItemsByRole(childItems: IChildItem[], user: UserResponse): IChildItem[] {
        return childItems.filter(item => {
            return (!item.roles || item.roles.some(role => user.role.includes(role)));
        });
    }
    updateMenuItems(): void {
        const filteredMenu = this.filterMenuItemsByUser(this.defaultMenu, this.user);
        this.menuItems.next(filteredMenu);
    }

    // You can customize this method to supply different menu for
    // different user type.
    // publishNavigationChange(menuType: string) {
    //   switch (userType) {
    //     case 'admin':
    //       this.menuItems.next(this.adminMenu);
    //       break;
    //     case 'user':
    //       this.menuItems.next(this.userMenu);
    //       break;
    //     default:
    //       this.menuItems.next(this.defaultMenu);
    //   }
    // }
}
